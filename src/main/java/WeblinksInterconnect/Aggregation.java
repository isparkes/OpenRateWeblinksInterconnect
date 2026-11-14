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
import OpenRate.process.AbstractAggregation;
import OpenRate.record.ErrorType;
import OpenRate.record.IRecord;
import OpenRate.record.RecordError;
import java.util.Calendar;

// Referenced classes of package WeblinksInterconnect:
//            WIRecord

public class Aggregation extends AbstractAggregation
{
  private final String AggFields[];

  public Aggregation()
  {
    AggFields = new String[5];
  }

  @Override
  public IRecord procValidRecord(IRecord r)
  {
    WIRecord CurrentRecord = (WIRecord)r;
    if(CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE)
    {
      AggFields[0] = CurrentRecord.PartnerOperator;
      AggFields[1] = CurrentRecord.CDRMonth;
      AggFields[2] = CurrentRecord.CallCase;
      AggFields[3] = Double.toString(CurrentRecord.Duration);
      AggFields[4] = Double.toString(CurrentRecord.RatedAmount);
      try
      {
        Aggregate(AggFields, CurrentRecord.AggFilter);
      }
      catch(ProcessingException pe)
      {
        CurrentRecord.addError(new RecordError("ERR_AGGREGATION_ERR", ErrorType.DATA_VALIDATION));
        getPipeLog().error("Error processing aggregation", pe);
      }
    }
    return r;
  }

  @Override
  public IRecord procErrorRecord(IRecord r)
  {
    return r;
  }

  // Set the Aggregation basename to something more unique than just the
  // Transaction number. We will set it to
  // Agg<baseName>_<transactionTimeinMs>.agg
  @Override
  public String getAggregationFileBaseName(int TransactionNumber)
  {
    String baseName = getBaseName(TransactionNumber);
    return (new StringBuilder()).append("Agg").append(baseName).append("_").append(Calendar.getInstance().getTimeInMillis()).append(".agg").toString();
  }
}
