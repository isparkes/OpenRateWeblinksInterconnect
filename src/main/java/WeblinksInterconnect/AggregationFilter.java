/* ====================================================================
 * Limited Evaluation License:
 *
 * This software is open source, but licensed. The license with this package
 * is an evaluation license, which may not be used for productive systems. If
 * you want a full license, please contact us.
 *
 * The exclusive owner of this work is the OpenRate project.
 * This work, including all associated documents and components
 * is Copyright of the OpenRate project 2006-2014.
 *
 * The following restrictions apply unless they are expressly relaxed in a
 * contractual agreement between the license holder or one of its officially
 * assigned agents and you or your organisation:
 *
 * 1) This work may not be disclosed, either in full or in part, in any form
 *    electronic or physical, to any third party. This includes both in the
 *    form of source code and compiled modules.
 * 2) This work contains trade secrets in the form of architecture, algorithms
 *    methods and technologies. These trade secrets may not be disclosed to
 *    third parties in any form, either directly or in summary or paraphrased
 *    form, nor may these trade secrets be used to construct products of a
 *    similar or competing nature either by you or third parties.
 * 3) This work may not be included in full or in part in any application.
 * 4) You may not remove or alter any proprietary legends or notices contained
 *    in or on this work.
 * 5) This software may not be reverse-engineered or otherwise decompiled, if
 *    you received this work in a compiled form.
 * 6) This work is licensed, not sold. Possession of this software does not
 *    imply or grant any right to you.
 * 7) You agree to disclose any changes to this work to the copyright holder
 *    and that the copyright holder may include any such changes at its own
 *    discretion into the work
 * 8) You agree not to derive other works from the trade secrets in this work,
 *    and that any such derivation may make you liable to pay damages to the
 *    copyright holder
 * 9) You agree to use this software exclusively for evaluation purposes, and
 *    that you shall not use this software to derive commercial profit or
 *    support your business or personal activities.
 *
 * This software is provided "as is" and any expressed or impled warranties,
 * including, but not limited to, the impled warranties of merchantability
 * and fitness for a particular purpose are disclaimed. In no event shall
 * The OpenRate Project or its officially assigned agents be liable to any
 * direct, indirect, incidental, special, exemplary, or consequential damages
 * (including but not limited to, procurement of substitute goods or services;
 * Loss of use, data, or profits; or any business interruption) however caused
 * and on theory of liability, whether in contract, strict liability, or tort
 * (including negligence or otherwise) arising in any way out of the use of
 * this software, even if advised of the possibility of such damage.
 * This software contains portions by The Apache Software Foundation, Robert
 * Half International.
 * ====================================================================
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
