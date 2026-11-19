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

import OpenRate.adapter.file.FlatFileInputAdapter;
import OpenRate.exception.ProcessingException;
import OpenRate.record.FlatRecord;
import OpenRate.record.HeaderRecord;
import OpenRate.record.IRecord;
import OpenRate.record.TrailerRecord;

public class AggLoaderInputAdapter extends FlatFileInputAdapter {

  public AggLoaderInputAdapter() {
  }

  /**
   * Nothing to do on the synthetic header record.
   *
   * @param r the header record
   * @return the record, unchanged
   */
  @Override
  public HeaderRecord procHeader(HeaderRecord r) throws ProcessingException {
    return r;
  }

  /**
   * All the work for this pipeline is done in the output adapter, which calls
   * the aggregation stored procedure, so the record passes through untouched.
   *
   * @param r the record read from the aggregation results file
   * @return the record, unchanged
   */
  @Override
  public IRecord procValidRecord(FlatRecord r) throws ProcessingException {
    return r;
  }

  /**
   * The flat file input adapter cannot create errored records, so there is
   * nothing to do here.
   *
   * @param r the record
   * @return the record, unchanged
   */
  @Override
  public IRecord procErrorRecord(FlatRecord r) throws ProcessingException {
    return r;
  }

  /**
   * Nothing to do on the synthetic trailer record.
   *
   * @param r the trailer record
   * @return the record, unchanged
   */
  @Override
  public TrailerRecord procTrailer(TrailerRecord r) throws ProcessingException {
    return r;
  }
}
