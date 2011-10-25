/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.walkaround.wave.client.profile;

import com.google.common.base.Joiner;
import com.google.gwt.http.client.URL;
import com.google.walkaround.util.client.log.Logs;
import com.google.walkaround.util.client.log.Logs.Level;
import com.google.walkaround.util.client.log.Logs.Log;
import com.google.walkaround.wave.client.rpc.Rpc;
import com.google.walkaround.wave.shared.ContactsService;
import com.google.walkaround.wave.shared.ContactsService.Contact;
import com.google.walkaround.wave.shared.ContactsService.ContactList;
import com.google.walkaround.wave.shared.SharedConstants;

import org.waveprotocol.wave.client.account.Profile;
import org.waveprotocol.wave.client.account.ProfileListener;
import org.waveprotocol.wave.client.account.ProfileManager;
import org.waveprotocol.wave.client.scheduler.Scheduler.Task;
import org.waveprotocol.wave.client.scheduler.SchedulerInstance;
import org.waveprotocol.wave.client.scheduler.TimerService;
import org.waveprotocol.wave.model.util.CollectionUtils;
import org.waveprotocol.wave.model.util.CopyOnWriteSet;
import org.waveprotocol.wave.model.util.StringMap;
import org.waveprotocol.wave.model.wave.InvalidParticipantAddress;
import org.waveprotocol.wave.model.wave.ParticipantId;

import java.util.List;
import java.util.Random;

/**
 * Uses a contacts service to provide profile information.
 *
 * @author hearnden@google.com (David Hearnden)
 */
public final class ContactsManager implements ProfileManager, ContactsService.Callback, Task {

  /**
   * A profile that can draw information from contacts.
   */
  private class ProfileImpl implements Profile {
    private final ParticipantId id;

    // These fields start off as guesses, then get replaced with contact data.
    private String firstName;
    private String fullName;
    private String photoUrl = SharedConstants.UNKNOWN_AVATAR_URL;

    private ProfileImpl(ParticipantId id) {
      this.id = id;
    }

    boolean updateWith(Contact contact) {
      boolean changed = false;
      if (contact.getName() != null) {
        fullName = contact.getName();
        int separator = fullName.indexOf(' ');
        firstName = separator != -1 ? fullName.substring(0, separator) : fullName;
        changed = true;
      }
      if (contact.getPhotoId() != null) {
        photoUrl = "/photos?photoId=" + URL.encodeQueryString(contact.getPhotoId());
        changed = true;
      }
      return changed;
    }

    @Override
    public ParticipantId getParticipantId() {
      return id;
    }

    @Override
    public String getAddress() {
      return id.getAddress();
    }

    @Override
    public String getFirstName() {
      if (firstName == null) {
        guessNames();
      }
      return firstName;
    }

    @Override
    public String getFullName() {
      if (fullName == null) {
        guessNames();
      }
      return fullName;
    }

    @Override
    public String getImageUrl() {
      return photoUrl;
    }

    private void guessNames() {
      assert firstName == null || fullName == null; // Only called from lazy
                                                    // loading.
      List<String> names = guessNames(id.getAddress());
      if (firstName == null) {
        firstName = names.get(0);
      }
      if (fullName == null) {
        fullName = Joiner.on(' ').join(names);
      }
    }

    private List<String> guessNames(String address) {
      List<String> names = CollectionUtils.newArrayList();
      String nameWithoutDomain = address.split("@")[0];
      // Include empty names from fragment, so split with a -ve.
      for (String fragment : nameWithoutDomain.split("[._]", -1)) {
        if (!fragment.isEmpty()) {
          names.add(capitalize(fragment));
        }
      }
      // ParticipantId normalization, and empty name inclusion, implies names
      // can not be empty.
      assert !names.isEmpty();
      return names;
    }

    private String capitalize(String s) {
      return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
  }

  /** Constructs a profile from nothing but a participant id. */
  private ProfileImpl profileFromThinAir(ParticipantId id) {
    return new ProfileImpl(id);
  }

  /** Constructs a profile from a contact. */
  private ProfileImpl profileFromContact(Contact c) throws InvalidParticipantAddress {
    ParticipantId id = ParticipantId.of(c.getAddress());
    ProfileImpl profile = new ProfileImpl(id);
    profile.updateWith(c);
    return profile;
  }

  private static final Log LOG = Logs.create("contacts");

  /** Maximum (total) number of contacts to fetch. */
  // This is an arbitrary maximum. Contact data is fairly small (50-100 bytes of
  // JSON per contact, which is ~100bytes per JS object on Chrome, ~400bytes per
  // JS object on Firefox, so 2000 of these should be fine.  The incremental
  // benefit to the quality of this profile service from contacts in the feed
  // beyond some large number (MAX_CONTACTS) is assumed to be small enough that
  // such contacts can be ignored.
  private static final int MAX_CONTACTS = 2000;
  /** Number of contacts to fetch on each call. */
  private static final int FETCH_SIZE = ContactsService.MAX_SIZE;
  /** How long to wait before fetching all contacts again (every 30m). */
  private static final int REFRESH_INTERVAL_MS = 30 * 60 * 1000;

  private final ContactsService service;
  private final TimerService timer;
  private final Random random;

  private final StringMap<Contact> contacts = CollectionUtils.createStringMap();
  private final StringMap<ProfileImpl> profiles = CollectionUtils.createStringMap();
  private final CopyOnWriteSet<ProfileListener> listeners = CopyOnWriteSet.create();
  private int feedIndex = 1; // Contacts service is 1-based, not 0-based.

  ContactsManager(ContactsService service, TimerService timer, Random random) {
    this.service = service;
    this.timer = timer;
    this.random = random;
  }

  /**
   * Creates a contacts manager. The manager will start fetching contacts
   * immediately.
   */
  public static ContactsManager create(Rpc rpc) {
    ContactsService service = new RemoteContactsService(rpc);
    TimerService timer = SchedulerInstance.getLowPriorityTimer();
    ContactsManager manager = new ContactsManager(service, timer, new Random());
    manager.fetchNext();
    return manager;
  }

  @Override
  public Profile getProfile(ParticipantId pid) {
    String id = pid.getAddress();
    ProfileImpl profile = profiles.get(id);
    if (profile == null) {
      if (contacts.containsKey(id)) {
        try {
          profile = profileFromContact(contacts.get(id));
        } catch (InvalidParticipantAddress e) {
          LOG.log(Level.WARNING, "Invalid contact address: ", contacts.get(id).getAddress());
          contacts.remove(id);
          profile = profileFromThinAir(pid);
        }
      } else {
        profile = profileFromThinAir(pid);
      }
      profiles.put(id, profile);
    }
    return profile;
  }

  /**
   * Adds contacts to the contact store, and updates any existing profiles.
   */
  private void updateWith(ContactList someContacts) {
    for (int i = 0; i < someContacts.size(); i++) {
      Contact contact = someContacts.get(i);
      String id = contact.getAddress();
      if (id == null) {
        // Empty contact; discard it.
        continue;
      }
      contacts.put(id, contact);

      ProfileImpl profile = profiles.get(id);
      if (profile != null) {
        profile.updateWith(contact);
        fireUpdates(profile);
      }
    }
  }

  //
  // Fetching.
  //

  private void fetchNext() {
    timer.schedule(this);
  }

  private void refreshLater() {
    feedIndex = 1;
    timer.scheduleDelayed(this, (int) ((0.9 + 0.2 * random.nextDouble()) * REFRESH_INTERVAL_MS));
  }

  @Override
  public void execute() {
    assert feedIndex < MAX_CONTACTS;
    LOG.log(Level.INFO, "Requesting " + FETCH_SIZE + " contacts");
    service.fetch(feedIndex, Math.min(feedIndex + FETCH_SIZE, MAX_CONTACTS), this);
  }

  //
  // ContactService callbacks.
  //

  @Override
  public void onSuccess(ContactList result) {
    LOG.log(Level.INFO, "Contact fetch succeeded with ", result.size(), " contacts");
    updateWith(result);
    feedIndex += result.size();

    // Fetch more contacts?
    if (result.size() == FETCH_SIZE) {
      if (feedIndex < MAX_CONTACTS) {
        fetchNext();
      } else {
        LOG.log(Level.INFO, "Finished fetching ", feedIndex, " contacts (max reached)");
        refreshLater();
      }
    } else {
      LOG.log(Level.INFO, "Finished fetching ", feedIndex, " contacts (no more contacts)");
      refreshLater();
    }
  }

  @Override
  public void onFailure() {
    LOG.log(Level.WARNING, "Contact fetch failed");
    // Start again later.
    refreshLater();
  }

  //
  // Events.
  //

  @Override
  public void addListener(ProfileListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(ProfileListener listener) {
    listeners.remove(listener);
  }

  private void fireUpdates(Profile... profiles) {
    // Note: if this gets too large, this should be extracted into an
    // incremental background task.
    for (ProfileListener listener : listeners) {
      for (int i = 0; i < profiles.length; i++) {
        listener.onProfileUpdated(profiles[i]);
      }
    }
  }

  @Override
  public boolean shouldIgnore(ParticipantId participantId) {
    return false;
  }
}
