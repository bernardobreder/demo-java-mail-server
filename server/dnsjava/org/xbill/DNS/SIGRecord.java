// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.text.*;
import java.util.*;
import org.xbill.DNS.utils.*;

/**
 * Signature - A SIG provides the digital signature of an RRset, so that the
 * data can be authenticated by a DNSSEC-capable resolver. The signature is
 * usually generated by a key contained in a KEYRecord
 * 
 * @see RRset
 * @see DNSSEC
 * @see KEYRecord
 *
 * @author Brian Wellington
 */

public class SIGRecord extends SIGBase {

  SIGRecord() {
  }

  Record getObject() {
    return new SIGRecord();
  }

  /**
   * Creates an SIG Record from the given data
   * 
   * @param covered The RRset type covered by this signature
   * @param alg The cryptographic algorithm of the key that generated the
   *        signature
   * @param origttl The original TTL of the RRset
   * @param expire The time at which the signature expires
   * @param timeSigned The time at which this signature was generated
   * @param footprint The footprint/key id of the signing key.
   * @param signer The owner of the signing key
   * @param signature Binary data representing the signature
   */
  public SIGRecord(Name name, int dclass, long ttl, int covered, int alg,
    long origttl, Date expire, Date timeSigned, int footprint, Name signer,
    byte[] signature) {
    super(name, Type.SIG, dclass, ttl, covered, alg, origttl, expire,
      timeSigned, footprint, signer, signature);
  }

}
