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

package com.google.walkaround.util.server.auth;

import org.apache.commons.codec.binary.Hex;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Additional digest utilities.
 *
 * @author Daniel Danilatos (danilatos@google.com)
 */
public class DigestUtils2 {

  public static final int SHA1_BLOCK_SIZE = 160 / 8;

  private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

  /**
   * Immutable type-safe wrapper for a secret key.
   */
  public static final class Secret {

    public static Secret of(byte[] secret) {
      return new Secret(Arrays.copyOf(secret, secret.length));
    }

    public static Secret generate(Random random) {
      byte[] bytes = new byte[SHA1_BLOCK_SIZE];
      random.nextBytes(bytes);
      return new Secret(bytes);
    }

    private final byte[] data;
    private final String asHex;

    // NOTE(danilatos) This is not safe to be a public constructor
    // as it does not copy the byte array.
    private Secret(byte[] bytes) {
      if (bytes.length > SHA1_BLOCK_SIZE) {
        // TODO(danilatos): Verify that this is not a problem and then
        // remove check.
        throw new IllegalArgumentException("Secret length " + bytes.length +
            " greater than block size " + SHA1_BLOCK_SIZE);
      }
      data = bytes;
      asHex = Hex.encodeHexString(data);
    }

    public String getHexData() {
      return asHex;
    }

    public byte[] getBytes() {
      // Copy to avoid exposing the mutable array.
      return Arrays.copyOf(data, data.length);
    }

    @Override
    public String toString() {
      // Purposefully don't actually show the secret data, in case it
      // gets accidentally logged or revealed in the wrong place.
      return "Secret(numBytes=" + data.length + ")";
    }
  }

  /**
   * Computes the RFC2104 SHA1 HMAC digest for the given message and secret.
   *
   * @param message the data to compute the digest of
   */
  public static byte[] sha1hmac(Secret key, byte[] message) {
    try {

      // get an hmac_sha1 key from the raw key bytes
      SecretKeySpec signingKey = new SecretKeySpec(key.data, HMAC_SHA1_ALGORITHM);

      // get an hmac_sha1 Mac instance and initialize with the signing key
      Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
      mac.init(signingKey);

      // compute the hmac on input data bytes
      byte[] ret = mac.doFinal(message);
      assert ret.length == SHA1_BLOCK_SIZE;
      return ret;

    } catch (InvalidKeyException e) {
      throw new RuntimeException("Failed to generate HMAC", e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to generate HMAC", e);
    }
  }

  public static String hexHmac(Secret key, String str) {
    return new String(Hex.encodeHex(
        DigestUtils2.sha1hmac(key, str.getBytes())));
  }
}
