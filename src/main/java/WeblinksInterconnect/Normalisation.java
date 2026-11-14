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
import java.util.ArrayList;

/**
 * This module maps the trunk IDs into trunk names for informational purposes.
 */
public class Normalisation
        extends AbstractRegexMatch {

  // this is used for the lookup
  String[] tmpSearchParameters = new String[1];

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
    String RegexGroup;
    WIRecord CurrentRecord;
    ArrayList<String> Results;

    CurrentRecord = (WIRecord) r;
    RecordError tmpError;

    if (CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
      // Prepare the paramters to perform the search on
      tmpSearchParameters[0] = CurrentRecord.Other_Party_Number;

      RegexGroup = "Default";

      Results = getRegexMatchWithChildData(RegexGroup, tmpSearchParameters);

      // Check we have some results
      if ((Results != null) & (Results.size() > 1)) {
        if (!Results.get(0).equalsIgnoreCase("nomatch")) {
          if (Results.get(0).isEmpty()) {
            // just add the prefix
            CurrentRecord.Other_Party_Number = Results.get(1) + CurrentRecord.Other_Party_Number;
          } else {
            // remove an old prefix and add the new prefix
            CurrentRecord.Other_Party_Number = CurrentRecord.Other_Party_Number.replaceAll(Results.get(0), Results.get(1));
          }
        } else {
          tmpError = new RecordError("ERR_NORMALISATION_LOOKUP", ErrorType.SPECIAL);
          CurrentRecord.addError(tmpError);
        }
      } else {
        tmpError = new RecordError("ERR_NORMALISATION_LOOKUP", ErrorType.SPECIAL);
        CurrentRecord.addError(tmpError);
      }
    }

    return r;
  }

  /**
   * This is called when a data record with errors is encountered. You should do
   * any processing here that you have to do for error records, e.g.
   * statistics, special handling, even error correction!
   *
   * @return
   */
  @Override
  public IRecord procErrorRecord(IRecord r) {
    return r;
  }

  // -----------------------------------------------------------------------------
  // ------------------------ Start of custom functions --------------------------
  // -----------------------------------------------------------------------------
}
