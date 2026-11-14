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

import OpenRate.adapter.jdbc.JDBCOutputAdapter;
import OpenRate.record.DBRecord;
import OpenRate.record.FlatRecord;
import OpenRate.record.IRecord;
import java.util.ArrayList;
import java.util.Collection;

public class AggLoaderDBOutputAdapter extends JDBCOutputAdapter {

  @Override
  public Collection<DBRecord> procValidRecord(IRecord r) {
    FlatRecord tmpInRecord;
    Collection<DBRecord> Outbatch = new ArrayList<>();

    // Get the data from the input record
    tmpInRecord = (FlatRecord) r;

    // Split it so that we can work on it
    String Fields[] = tmpInRecord.getData().split(";");

    // Create a new DB record
    DBRecord tmpDataRecord = new DBRecord();
    tmpDataRecord.setOutputColumnCount(6);

    // Set the DB record columns with the input field values
    tmpDataRecord.setOutputColumnString(0, Fields[1]);
    tmpDataRecord.setOutputColumnString(1, Fields[2]);
    tmpDataRecord.setOutputColumnString(2, Fields[3]);
    tmpDataRecord.setOutputColumnString(3, Fields[4]);
    tmpDataRecord.setOutputColumnString(4, Fields[5]);
    tmpDataRecord.setOutputColumnString(5, Fields[6]);

    // Add the record to the output batch
    Outbatch.add(tmpDataRecord);

    // Finished
    return Outbatch;
  }

  @Override
  public Collection<DBRecord> procErrorRecord(IRecord r) {
    // Do nothing
    return null;
  }
}
