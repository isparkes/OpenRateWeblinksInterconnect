/*
 * Copyright 2006-2026 Ian Sparkes
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
package WeblinksInterconnect;

import OpenRate.process.AbstractValiditySegmentMatch;
import OpenRate.record.IRecord;

/**
 * This module allows rating to be performed differently for the same model at
 * different times. In effect, in this module we re-map the result of the zone
 * model to another zone model, based on the time of the CDR. This allows the
 * terminating prices to be changed at 1st April 2008.
 *
 * If no mapping is provided, the standard zone model is used as a pass though.
 *
 * @author ian
 */
public class ZoneModelValidity extends AbstractValiditySegmentMatch {

  /**
   * This is called when a data record is encountered. You should do any normal
   * processing here.
   *
   * @param r The record we are working on
   * @return The processed record
   */
  @Override
  public IRecord procValidRecord(IRecord r) {
    WIRecord CurrentRecord = (WIRecord) r;
    String NewZoneValue;

    // We only transform the basic records, and leave the others alone
    if (CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
      // Look up the Destination
      NewZoneValue = getValiditySegmentMatch("Default", CurrentRecord.ZoneModel, CurrentRecord.utcEventDate);

      // Write the information back into the record
      if (!NewZoneValue.equalsIgnoreCase("nomatch")) {
        // There was a remapping
        CurrentRecord.ZoneModel = NewZoneValue;
      }
    }

    return r;
  }

  /**
   * This is called when a data record with errors is encountered. You should do
   * any processing here that you have to do for error records, e.g. statistics,
   * special handling, even error correction!
   *
   * @param r The record we are working on
   * @return The processed record
   */
  @Override
  public IRecord procErrorRecord(IRecord r) {
    // Do nothing
    return r;
  }
}
