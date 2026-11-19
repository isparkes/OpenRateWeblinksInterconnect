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
import OpenRate.exception.InitializationException;
import OpenRate.exception.ProcessingException;
import OpenRate.record.*;
import OpenRate.utils.PropertyUtils;
import WeblinksInterconnect.WIRecord;
import OpenRate.record.FlatRecord;
import OpenRate.record.HeaderRecord;
import OpenRate.record.IRecord;
import OpenRate.record.TrailerRecord;

/**
 * This class is an example of how one would write an InputAdapter. An input
 * adapter implements a single loadBatch() method that selects a set of work and
 * returns it. The framework takes the Collection & pushes it into the first
 * channel where the pipeline begins work on it. Typically the loadBatch()
 * method would read records from a Messaging System, or a DB Table, or a flat
 * file, but in this simplest possible case, it just create a bunch of records
 * out of thin air and passes them on to the pipeline.
 */
public class WIInputAdapter extends FlatFileInputAdapter {

  // This is the stream record number counter which tells us the number of
  // the compressed records
  private int StreamRecordNumber;

  // Calls shorter than this will be discarded
  private int MinDuration = 0;

  // The SQL statements from the properties that are used to get the records
  private static final String MIN_DUR_KEY = "MinimumDuration";

  // This is the object that is used to compress the records
  WIRecord tmpDataRecord = null;

  /**
   * Constructor for InputAdapter.
   */
  public WIInputAdapter() {
    super();
  }

  // -----------------------------------------------------------------------------
  // ------------------ Start of inherited Plug In functions ---------------------
  // -----------------------------------------------------------------------------
  // -----------------------------------------------------------------------------
  // ------------------ Start of inherited Plug In functions ---------------------
  // -----------------------------------------------------------------------------
  @Override
  public void init(String PipelineName, String ModuleName) throws InitializationException {
    String strMinDuration;

    super.init(PipelineName, ModuleName);

    // Get the process date format from the input adapter configuration
    strMinDuration = PropertyUtils.getPropertyUtils().getBatchInputAdapterPropertyValueDef(PipelineName, getSymbolicName(), MIN_DUR_KEY, "None");

    if ((strMinDuration != null) & !strMinDuration.equalsIgnoreCase("None")) {
      MinDuration = Integer.parseInt(strMinDuration);
      getPipeLog().info("Setting minimum duration for pipeline <" + PipelineName + "> to <" + MinDuration + ">");
    }
  }

  /**
   * This is called when the synthetic Header record is encountered, and has the
   * meaning that the stream is starting. In this example we have nothing to do
   *
   * @return
   */
  @Override
  public HeaderRecord procHeader(HeaderRecord r) {
    // reset the record numbering
    StreamRecordNumber = 0;

    return r;
  }

  /**
   * This is called when a data record is encountered. You should do any normal
   * processing here. For the input adapter, we probably want to change the
   * record type from FlatRecord to the record(s) type that we will be using in
   * the processing pipeline.
   *
   * This is also the location for accumulating records into logical groups
   * (that is records with sub records) and placing them in the pipeline as they
   * are completed. If you receive a sub record, simply return a null record in
   * this method to indicate that you are handling it, and that it will be
   * purged at a later date.
   *
   * @return
   */
  @Override
  public IRecord procValidRecord(FlatRecord r) {
    String tmpData;
    RecordError tmpError;
    boolean MapResult;

    // Perform the numbering of the record in the stream, and change the type
    // of the record to "RateRecord". The decision of the type of record is 
    // made in the "ServiceLookup" processing step.
    tmpData = r.getData();
    tmpDataRecord = new WIRecord();

    tmpDataRecord.setOriginalData(tmpData);
    MapResult = tmpDataRecord.mapRecord();

    if (MapResult == false) {
      // the mapping failed miserably, throw the file to error
      this.setTransactionAbort(getTransactionNumber());
    }

    tmpDataRecord.recordNumber = StreamRecordNumber++;

    // discard the call if the duration was too short
    if (tmpDataRecord.Duration < MinDuration) {
      tmpError = new RecordError("ERR_CALL_TOO_SHORT", ErrorType.DATA_VALIDATION);
      tmpDataRecord.addError(tmpError);
    }

    return tmpDataRecord;
  }

  /**
   * This is called when a data record with errors is encountered. You should do
   * any processing here that you have to do for error records, e.g. statistics,
   * special handling, even error correction!
   *
   * The input adapter is not expected to provide any records here.
   *
   * @return
   */
  @Override
  public IRecord procErrorRecord(FlatRecord r) {

    // The FlatFileInputAdapter is not able to create error records, so we
    // do not have to do anything for this
    return r;
  }

  /**
   * This is called when the synthetic trailer record is encountered, and has
   * the meaning that the stream is now finished. In this example, all we do is
   * pass the control back to the transactional layer.
   *
   * In models where record aggregation (records and sub records) is used, you
   * might want to check for any purged records here.
   *
   * @return
   */
  @Override
  public TrailerRecord procTrailer(TrailerRecord r) {
    // set the trailer record count
    r.setRecordCount(StreamRecordNumber);
    return r;
  }

}
