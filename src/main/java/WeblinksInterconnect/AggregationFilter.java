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

import OpenRate.exception.InitializationException;
import OpenRate.process.AbstractRegexMatch;
import OpenRate.record.IRecord;
import OpenRate.utils.PropertyUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class AggregationFilter extends AbstractRegexMatch
{
    private final GregorianCalendar tmpCal;
    private final SimpleDateFormat sdfIn;
    private final SimpleDateFormat sdfOut;
    private int BillingOffset;
    private Date ThisMonthStart;
    private Date OffsetDate;
    private Date LastMonthStart;
    private int Index;

    public AggregationFilter()
    {
      tmpCal = new GregorianCalendar();
      sdfIn = new SimpleDateFormat("yyyyMMdd");
      sdfOut = new SimpleDateFormat("yyyyMM");
    }

  @Override
  public void init(String PipelineName, String ModuleName)
      throws InitializationException
  {
    super.init(PipelineName, ModuleName);

    String VirtualDate = PropertyUtils.getPropertyUtils().getPluginPropertyValueDef(PipelineName, ModuleName, "VirtualDate", "None");
    if(!VirtualDate.equals("None")) {
      try
      {
        Date NewDate = sdfIn.parse(VirtualDate);
        tmpCal.setTime(NewDate);
      }
      catch(ParseException ex)
      {
        message = (new StringBuilder()).append("Unable to parse virtual date in <").append(getSymbolicName()).append(">. Was expecting YYYYMMDD.").toString();
        getPipeLog().error(message);
        throw new InitializationException(message,getSymbolicName());
      }
    }

    String OffsetStr = PropertyUtils.getPropertyUtils().getPluginPropertyValueDef(PipelineName, ModuleName, "BillingOffset", "None");
    if(OffsetStr.equals("None")) {
      BillingOffset = 4;
    } else {
      try
      {
        BillingOffset = Integer.parseInt(OffsetStr);
      }
      catch(NumberFormatException ex)
      {
        message = (new StringBuilder()).append("Unable to parse billing offset value in <").append(getSymbolicName()).append(">. Was expecting YYYYMMDD.").toString();
        getPipeLog().error(message);
        throw new InitializationException(message,getSymbolicName());
      }
    }

    tmpCal.set(5, 1);
    tmpCal.set(10, 0);
    tmpCal.set(12, 0);
    tmpCal.set(13, 0);
    tmpCal.set(14, 0);
    ThisMonthStart = tmpCal.getTime();
    tmpCal.add(2, -1);
    LastMonthStart = tmpCal.getTime();
    tmpCal.add(2, 1);
    tmpCal.add(10, BillingOffset * 24);
    OffsetDate = tmpCal.getTime();
  }

  @Override
  public IRecord procValidRecord(IRecord r)
  {
    // get access to the record
    WIRecord CurrentRecord = (WIRecord)r;
    
    if(CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE)
    {
      for(Index = 0; Index < CurrentRecord.getChargePacketCount(); Index++) {
        if(CurrentRecord.getChargePacket(Index).resource.equals("EUR"))
        {
          CurrentRecord.RatedAmount = (float)CurrentRecord.getChargePacket(Index).chargedValue;
          CurrentRecord.RatedAmount = Math.round(CurrentRecord.RatedAmount * 1000D);
          CurrentRecord.RatedAmount /= 1000D;
        }
      }

      String tmpSearchParameters[] = new String[1];
      tmpSearchParameters[0] = CurrentRecord.CallCase;
      String RegexGroup = "Default";
      CurrentRecord.AggFilter = getAllEntries(RegexGroup, tmpSearchParameters);
    }
    return r;
  }

  @Override
  public IRecord procErrorRecord(IRecord r)
  {
    return r;
  }
}
