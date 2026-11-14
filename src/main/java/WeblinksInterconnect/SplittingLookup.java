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

import OpenRate.process.AbstractRegexMatch;
import OpenRate.record.ErrorType;
import OpenRate.record.IRecord;
import OpenRate.record.RecordError;

/**
 * This module looks up the output that the traffic should go to based on the
 * call case and the contract
 */
public class SplittingLookup
        extends AbstractRegexMatch {

  // this is used for the lookup

  String[] tmpSearchParameters = new String[3];

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
    return FindOutput(r);
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
    return FindOutput(r);
  }

  // -----------------------------------------------------------------------------
  // ------------------------ Start of custom functions --------------------------
  // -----------------------------------------------------------------------------
  public IRecord FindOutput(IRecord r) {
    String RegexGroup;
    String RegexResult;
    WIRecord CurrentRecord;
    RecordError tmpError;
    String ErrorDescription = "";

    CurrentRecord = (WIRecord) r;

    if (CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
      // see if the record has errors
      if (CurrentRecord.getErrors().size() > 0) {
        // if so, get the first error
        ErrorDescription = CurrentRecord.getErrors().get(0).getMessage();
      }

      // Prepare the paramters to perform the search on
      tmpSearchParameters[0] = CurrentRecord.CallCase;
      tmpSearchParameters[1] = CurrentRecord.ZoneModel;
      tmpSearchParameters[2] = ErrorDescription;

      RegexGroup = "Splitting";
      RegexResult = getRegexMatch(RegexGroup, tmpSearchParameters);

      if (!RegexResult.equalsIgnoreCase("nomatch")) {
        // Add the outputs which we need
        String[] outputs = RegexResult.split(",");

        for (String output : outputs) {
          CurrentRecord.addOutput(output);
        }
      } else {
        CurrentRecord.addOutput("Error");
        tmpError = new RecordError("ERR_SPLITTING_LOOKUP", ErrorType.SPECIAL, getSymbolicName());
        CurrentRecord.addError(tmpError);
      }
    }

    return r;
  }
}
