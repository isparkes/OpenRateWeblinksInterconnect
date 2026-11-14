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
import OpenRate.process.AbstractDuplicateCheck;
import OpenRate.record.ErrorType;
import OpenRate.record.IRecord;
import OpenRate.record.RecordError;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a bit of a fat Filter, doing all of the Pollux logic evaluation
 * and selection in one go. It uses information from three cache objects to
 * evaluate the logic candidate to use for the rating
 */
public class DupCheck extends AbstractDuplicateCheck {

  /**
   * This is called when a data record is encountered. You should do any normal
   * processing here.
   *
   * @return
   */
  @Override
  public IRecord procValidRecord(IRecord r) {
    WIRecord CurrentRecord;
    RecordError tmpError;

    CurrentRecord = (WIRecord) r;

    if (CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
      try {
        if (CheckDuplicate(CurrentRecord.eventStartDate, CurrentRecord.DupChkKey)) {
          // error detected, add duplicate error to the record
          tmpError = new RecordError("ERR_DUPLICATE_RECORD", ErrorType.SPECIAL);
          CurrentRecord.addError(tmpError);
        }
      } catch (ProcessingException ex) {
        Logger.getLogger(DupCheck.class.getName()).log(Level.SEVERE, null, ex);
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
