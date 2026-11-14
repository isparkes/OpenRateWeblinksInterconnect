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
import OpenRate.record.IRecord;

/**
 * This module maps the trunk IDs into trunk names for informational purposes.
 */
public class TrunkLookup
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
    String RegexResult;
    WIRecord CurrentRecord;

    CurrentRecord = (WIRecord) r;

    if (CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
      // Prepare the paramters to perform the search on
      tmpSearchParameters[0] = CurrentRecord.TrunkIn;

      RegexGroup = "TrunkMap";

      RegexResult = getRegexMatch(RegexGroup, tmpSearchParameters);

      if (!RegexResult.equalsIgnoreCase("nomatch")) {
        if (RegexResult.equalsIgnoreCase("none")) {
          CurrentRecord.TrunkInName = "";
        } else {
          CurrentRecord.TrunkInName = RegexResult;
        }
      }

      tmpSearchParameters[0] = CurrentRecord.TrunkOut;

      RegexGroup = "TrunkMap";

      RegexResult = getRegexMatch(RegexGroup, tmpSearchParameters);

      if (!RegexResult.equalsIgnoreCase("nomatch")) {
        if (RegexResult.equalsIgnoreCase("none")) {
          CurrentRecord.TrunkOutName = "";
        } else {
          CurrentRecord.TrunkOutName = RegexResult;
        }
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

  // -----------------------------------------------------------------------------
  // ------------------------ Start of custom functions --------------------------
  // -----------------------------------------------------------------------------
}
