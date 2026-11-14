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

import OpenRate.process.AbstractIndexedLookupMatch;
import OpenRate.record.ErrorType;
import OpenRate.record.IRecord;
import OpenRate.record.RecordError;

/**
 * This class is an example of the use of the rating processing module/data
 * cache pair. It uses the information from the data cache to calculate the cost
 * of a record according to the rating you define.
 */
public class OperatorLookup
        extends AbstractIndexedLookupMatch {

  String[] ILCResults = new String[2];

  // -----------------------------------------------------------------------------
  // ------------------ Start of inherited Plug In functions ---------------------
  // -----------------------------------------------------------------------------
  /**
   * For each of the rate plans, see if it is valid for this record, and assign
   * a priority to it.
   *
   * @return
   */
  @Override
  public IRecord procValidRecord(IRecord r) {
    RecordError tmpError;
    String OperatorName;
    WIRecord CurrentRecord = (WIRecord) r;

    try {
      // We only transform the basic records, and leave the others alone
      if (CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
        // Look up the Destination
        switch (CurrentRecord.Direction) {
          case "Originating":
            ILCResults = ILC.getEntry(0, CurrentRecord.TrunkOutName);
            OperatorName = ILCResults[1];
            break;
          case "Terminating":
            ILCResults = ILC.getEntry(0, CurrentRecord.TrunkInName);
            OperatorName = ILCResults[1];
            break;
          default:
            OperatorName = "Unknown";
            break;
        }

        // Write the information back into the record
        CurrentRecord.PartnerOperator = OperatorName;
      }
    } catch (Exception e) {
      // error detected, add an error to the record
      tmpError = new RecordError("ERR_OPERATOR_LOOKUP", ErrorType.SPECIAL, getSymbolicName());
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
