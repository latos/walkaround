/**
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.waveprotocol.box.server;

import org.waveprotocol.wave.util.settings.Setting;

import java.util.List;

/**
 * Core Wave in a Box settings
 */
@SuppressWarnings("unused") // We inject them by the name of their flag
public class CoreSettings {
  public static final String WAVE_SERVER_DOMAIN = "wave_server_domain";
  public static final String HTTP_FRONTEND_PUBLIC_ADDRESS = "http_frontend_public_address";
  public static final String HTTP_FRONTEND_ADDRESSES = "http_frontend_addresses";
  public static final String RESOURCE_BASES = "resource_bases";
  public static final String WAVESERVER_DISABLE_VERIFICATION = "waveserver_disable_verification";
  public static final String WAVESERVER_DISABLE_SIGNER_VERIFICATION =
      "waveserver_disable_signer_verification";
  public static final String ENABLE_FEDERATION = "enable_federation";
  public static final String SIGNER_INFO_STORE_TYPE = "signer_info_store_type";
  public static final String SIGNER_INFO_STORE_DIRECTORY = "signer_info_store_directory";
  public static final String ATTACHMENT_STORE_TYPE = "attachment_store_type";
  public static final String ATTACHMENT_STORE_DIRECTORY = "attachment_store_directory";
  public static final String ACCOUNT_STORE_TYPE = "account_store_type";
  public static final String ACCOUNT_STORE_DIRECTORY = "account_store_directory";
  public static final String DELTA_STORE_TYPE = "delta_store_type";
  public static final String DELTA_STORE_DIRECTORY = "delta_store_directory";
  public static final String SESSIONS_STORE_DIRECTORY = "sessions_store_directory";
  public static final String FLASHSOCKET_POLICY_PORT = "flashsocket_policy_port";
  public static final String USE_SOCKETIO = "use_socketio";
  public static final String GADGET_SERVER_HOSTNAME = "gadget_server_hostname";
  public static final String GADGET_SERVER_PORT = "gadget_server_port";
  public static final String GADGET_SERVER_PATH = "gadget_server_path";
  public static final String ADMIN_USER = "admin_user";
  public static final String  WELCOME_WAVE_ID = "welcome_wave_id";

  @Setting(name = WAVE_SERVER_DOMAIN)
  private static String waveServerDomain;

  @Setting(name = HTTP_FRONTEND_PUBLIC_ADDRESS, defaultValue = "localhost:9898",
      description = "The server's public address.")
  private static String httpFrontEndPublicAddress;

  @Setting(name = HTTP_FRONTEND_ADDRESSES, defaultValue = "localhost:9898",
      description = "A comman seperated list of address on which to listen for connections."
          + " Each address is a host or ip and port seperated by a colon.")
  private static List<String> httpFrontEndAddresses;

  @Setting(name = RESOURCE_BASES, defaultValue = "./war",
      description = "The server's resource base directory list.")
  private static List<String> resourceBases;

  @Setting(name = WAVESERVER_DISABLE_VERIFICATION)
  private static boolean waveserverDisableVerification;

  @Setting(name = WAVESERVER_DISABLE_SIGNER_VERIFICATION)
  private static boolean waveserverDisableSignerVerification;

  @Setting(name = ENABLE_FEDERATION, defaultValue = "false")
  private static boolean enableFederation;

  @Setting(name = SIGNER_INFO_STORE_TYPE,
      description = "Type of persistence to use for the SignerInfo Storage",
      defaultValue = "memory")
  private static String signerInfoStoreType;

  @Setting(name = SIGNER_INFO_STORE_DIRECTORY,
      description = "Location on disk where the signer info store lives. Must be writeable by the "
          + "wave-in-a-box process. Only used by file-based signer info store.",
      defaultValue = "_certificates")
  private static String signerInfoStoreDirectory;

  @Setting(name = ATTACHMENT_STORE_TYPE,
      description = "Type of persistence store to use for attachments", defaultValue = "disk")
  private static String attachmentStoreType;

  @Setting(name = ATTACHMENT_STORE_DIRECTORY,
      description = "Location on disk where the attachment store lives. Must be writeable by the "
          + "fedone process. Only used by disk-based attachment store.",
      defaultValue = "_attachments")
  private static String attachmentStoreDirectory;

  @Setting(name = ACCOUNT_STORE_TYPE,
      description = "Type of persistence to use for the accounts", defaultValue = "memory")
  private static String accountStoreType;

  @Setting(name = ACCOUNT_STORE_DIRECTORY,
      description = "Location on disk where the account store lives. Must be writeable by the "
          + "wave-in-a-box process. Only used by file-based account store.",
      defaultValue = "_accounts")
  private static String accountStoreDirectory;

  @Setting(name = DELTA_STORE_TYPE,
      description = "Type of persistence to use for the deltas", defaultValue = "memory")
  private static String deltaStoreType;

  @Setting(name = DELTA_STORE_DIRECTORY,
      description = "Location on disk where the delta store lives. Must be writeable by the "
          + "wave-in-a-box process. Only used by file-based account store.",
      defaultValue = "_deltas")
  private static String deltaStoreDirectory;

  @Setting(name = SESSIONS_STORE_DIRECTORY,
      description = "Location on disk where the user sessions are persisted. Must be writeable by the "
          + "wave-in-a-box process.",
      defaultValue = "_sessions")
  private static String sessionsStoreDirectory;

  @Setting(name = FLASHSOCKET_POLICY_PORT,
      description = "Port on which to listen for Flashsocket policy requests.",
      defaultValue = "843")
  private static int flashsocketPolicyPort;

  @Setting(name = USE_SOCKETIO,
      description = "Enable use of Socket.IO instead of native WebSockets", defaultValue = "false")
  private static boolean useSocketIO;

  @Setting(name = GADGET_SERVER_HOSTNAME, description = "The hostname of the gadget server.",
      defaultValue = "gmodules.com")
  private static String gadgetServerHostname;

  @Setting(name = GADGET_SERVER_PORT, description = "The port of the gadget server.",
      defaultValue = "80")
  private static int gadgetServerPort;

  @Setting(name = GADGET_SERVER_PATH, description = "The URL path of the gadget server.",
      defaultValue = "/gadgets")
  private static String gadgetServerPath;

  @Setting(name = ADMIN_USER, description = "The admin user id for this server.",
      defaultValue = "@example.com")
  private static String adminUser;

  @Setting(name = WELCOME_WAVE_ID, description = "The welcome wave id.",
      defaultValue = "UNDEFINED")
  private static String welcomeWaveId;
}
