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

import OpenRate.process.AbstractBestMatch;
import OpenRate.record.ChargePacket;
import OpenRate.record.TimePacket;
import OpenRate.record.ErrorType;
import OpenRate.record.IRecord;
import OpenRate.record.RecordError;
import java.util.ArrayList;

/**
 * This class is an example of a plug in that does only a lookup, and thus does
 * not need to be registered as transaction bound. Recall that we will only need
 * to be transaction aware when we need some specific information from the
 * transaction management (e.g. the base file name) or when we require to have
 * the possibility to undo transaction work in the case of some failure.
 *
 * In this case we do not need it, as the input and output adapters will roll
 * the information back for us (by removing the output stream) in the case of an
 * error.
 */
public class ZoneLookup extends AbstractBestMatch {
  // -----------------------------------------------------------------------------
  // ------------------ Start of inherited Plug In functions ---------------------
  // -----------------------------------------------------------------------------

  /**
   * This is called when a data record is encountered. You should do any normal
   * processing here.
   *
   * This transformation looks up the zone name prefix using the best match
   * ZoneCache lookup. Because this example does not care about services, we
   * define the service type as a default "DEF".
   *
   * @return
   */
  @Override
  public IRecord procValidRecord(IRecord r) {
    RecordError tmpError;
    ArrayList<String> ZoneValue;
    WIRecord CurrentRecord = (WIRecord) r;
    ChargePacket tmpCP;
    TimePacket tmpTZ;

    try {
      // We only transform the basic records, and leave the others alone
      if (CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
        // Look up the Destination
        ZoneValue = getBestMatchWithChildData(CurrentRecord.ZoneModel, CurrentRecord.Other_Party_Number);

        // Write the information back into the record
        CurrentRecord.ZoneResult = ZoneValue.get(0);
        CurrentRecord.DestDescription = ZoneValue.get(1);

        if (ZoneValue.get(0).equals("NOMATCH")) {
          tmpError = new RecordError("ERR_ZONE_LOOKUP", ErrorType.SPECIAL);
          CurrentRecord.addError(tmpError);
        } else {
          // we have the zone, create the charge packet
          tmpCP = new ChargePacket();
          tmpCP.zoneModel = CurrentRecord.ZoneModel;
          tmpCP.zoneResult = CurrentRecord.ZoneResult;
          tmpCP.timeModel = "ALL";
          tmpCP.timeResult = "ANY";
          tmpCP.packetType = "I";
          tmpCP.rumName = "DUR";
          tmpCP.rumQuantity = CurrentRecord.Duration;
          tmpCP.ratePlanName = CurrentRecord.ZoneResult;
          tmpCP.service = "Voice";
          tmpCP.subscriptionID = "";

          // The price group lives on a time packet inside the charge packet,
          // so that a tariff can change during a call. Interconnect rating
          // here has no time dependency, so there is exactly one time packet
          // covering the whole call. The zone lookup returns the name of the
          // price model to use, which is the price group for the RUM map.
          tmpTZ = new TimePacket();
          tmpTZ.priceGroup = CurrentRecord.ZoneResult;
          tmpTZ.timeModel = "ALL";
          tmpTZ.timeResult = "ANY";
          tmpTZ.duration = (int) CurrentRecord.Duration;
          tmpTZ.totalDuration = (int) CurrentRecord.Duration;
          tmpCP.addTimeZone(tmpTZ);

          CurrentRecord.addChargePacket(tmpCP);
        }
      }
    } catch (Exception e) {
      // error detected, add an error to the record
      tmpError = new RecordError("ERR_ZONE_LOOKUP_FAILURE", ErrorType.SPECIAL);
      CurrentRecord.addError(tmpError);
    }

    return r;
  }

  /**
   * This is called when a data record with errors is encountered. You should do
   * any processing here that you have to do for error records, e.g. statistics,
   * special handling, even error correction!
   *
   * @return
   */
  @Override
  public IRecord procErrorRecord(IRecord r) {
    return r;
  }
}
