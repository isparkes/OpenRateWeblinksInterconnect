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

/**
 * Look up the contract based on the input and output trunk. This determines
 * both the Interconnection contract and the zone model to use.
 * 
 * @author TGDSPIA1
 */
import OpenRate.process.AbstractRegexMatch;
import OpenRate.record.ErrorType;
import OpenRate.record.IRecord;
import OpenRate.record.RecordError;

public class TrunkICLookup extends AbstractRegexMatch
{
  String tmpSearchParameters[];

  public TrunkICLookup()
  {
    tmpSearchParameters = new String[2];
  }

  @Override
  public IRecord procValidRecord(IRecord r)
  {
    WIRecord CurrentRecord = (WIRecord)r;
    
    if(CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE)
    {
      tmpSearchParameters[0] = CurrentRecord.TrunkIn;
      tmpSearchParameters[1] = CurrentRecord.TrunkOut;
      String RegexGroup = "TrunkICMap";
      String RegexResult = getRegexMatch(RegexGroup, tmpSearchParameters);
      if(!RegexResult.equalsIgnoreCase("nomatch"))
      {
        if(RegexResult.equalsIgnoreCase("none"))
        {
          CurrentRecord.ZoneModel = "Unknown";
          RecordError tmpError = new RecordError("ERR_IC_LOOKUP", ErrorType.SPECIAL, getSymbolicName());
          CurrentRecord.addError(tmpError);
        } else
        {
          CurrentRecord.ZoneModel = RegexResult;
        }
      } else
      {
        CurrentRecord.ZoneModel = "Unknown";
        RecordError tmpError = new RecordError("ERR_IC_LOOKUP", ErrorType.SPECIAL, getSymbolicName());
        CurrentRecord.addError(tmpError);
      }
    }
    return r;
  }

  @Override
  public IRecord procErrorRecord(IRecord r)
  {
      return r;
  }
}
