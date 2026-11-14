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

//  // REMOVED --> @Override
//  public IRecord procValidRecord(IRecord r) {
//      // We don't need to work on the record here, all work is done in
//    // the output adapter, so just pass the record back untouched
//    return r;
//  }
//
//  // REMOVED --> @Override
//  public IRecord procErrorRecord(IRecord r) {
//    // Do nothing - the input adapter cannot created errored records
//    return r;
//  }
//
//  // REMOVED --> @Override
//  public IRecord procHeader(IRecord r) throws ProcessingException {
//    // Do nothing
//    return r;
//  }
//
//  // REMOVED --> @Override
//  public IRecord procTrailer(IRecord r) throws ProcessingException {
//    // Do nothing
//    return r;
//  }

@Override
public IRecord procValidRecord(FlatRecord r) throws ProcessingException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public IRecord procErrorRecord(FlatRecord r) throws ProcessingException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public HeaderRecord procHeader(HeaderRecord r) throws ProcessingException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public TrailerRecord procTrailer(TrailerRecord r) throws ProcessingException {
	// TODO Auto-generated method stub
	return null;
}
}
