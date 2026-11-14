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

import OpenRate.exception.ProcessingException;
import OpenRate.process.AbstractRUMRateCalc;
import OpenRate.record.ChargePacket;
import OpenRate.record.ErrorType;
import OpenRate.record.IRecord;
import OpenRate.record.RecordError;

/**
 * This class uses the RUM (Rateable Usage Metric) based rating to calculate the
 * charges for the record.
 */
public class RateLookup extends AbstractRUMRateCalc {
  // -----------------------------------------------------------------------------
  // ------------------ Start of inherited Plug In functions ---------------------
  // -----------------------------------------------------------------------------

  /**
   * This is called when a data record is encountered. You should do any normal
   * processing here.
   *
   * @return
   */
  @Override
  public IRecord procValidRecord(IRecord r) {
    int Index;
    ChargePacket tmpCP;
    WIRecord CurrentRecord;
    RecordError tmpError;

    CurrentRecord = (WIRecord) r;

    if (CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
      try {
        performRating(CurrentRecord);
      } catch (ProcessingException pe) {
        tmpError = new RecordError("ERR_RATE_LOOKUP", ErrorType.SPECIAL);
        CurrentRecord.addError(tmpError);
      }

      // get the rated value
      for (Index = 0; Index < CurrentRecord.getChargePacketCount(); Index++) {
        tmpCP = CurrentRecord.getChargePacket(Index);
        CurrentRecord.RatedAmount += tmpCP.chargedValue;
      }
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
