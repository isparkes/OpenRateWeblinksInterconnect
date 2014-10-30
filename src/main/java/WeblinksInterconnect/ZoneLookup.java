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

import OpenRate.process.AbstractBestMatch;
import OpenRate.record.ChargePacket;
import OpenRate.record.ErrorType;
import OpenRate.record.IRecord;
import OpenRate.record.RecordError;
import java.util.ArrayList;

/**
 * This class is an example of a plug in that does only a lookup, and thus does
 * not need to be registered as transaction bound. Recall that we will only need
 * to be transaction aware when we need some specific information from the
 * transaction management (e.g. the base file name) or when we require to have
 * the possibility to undo transaction work in the case of some failure.
 *
 * In this case we do not need it, as the input and output adapters will roll
 * the information back for us (by removing the output stream) in the case of an
 * error.
 */
public class ZoneLookup extends AbstractBestMatch {
  // -----------------------------------------------------------------------------
  // ------------------ Start of inherited Plug In functions ---------------------
  // -----------------------------------------------------------------------------

  /**
   * This is called when a data record is encountered. You should do any normal
   * processing here.
   *
   * This transformation looks up the zone name prefix using the best match
   * ZoneCache lookup. Because this example does not care about services, we
   * define the service type as a default "DEF".
   *
   * @return
   */
  @Override
  public IRecord procValidRecord(IRecord r) {
    RecordError tmpError;
    ArrayList<String> ZoneValue;
    WIRecord CurrentRecord = (WIRecord) r;
    ChargePacket tmpCP;

    try {
      // We only transform the basic records, and leave the others alone
      if (CurrentRecord.RECORD_TYPE == WIRecord.WI_RECORD_TYPE) {
        // Look up the Destination
        ZoneValue = getBestMatchWithChildData(CurrentRecord.ZoneModel, CurrentRecord.Other_Party_Number);

        // Write the information back into the record
        CurrentRecord.ZoneResult = ZoneValue.get(0);
        CurrentRecord.DestDescription = ZoneValue.get(1);

        if (ZoneValue.get(0).equals("NOMATCH")) {
          tmpError = new RecordError("ERR_ZONE_LOOKUP", ErrorType.SPECIAL);
          CurrentRecord.addError(tmpError);
        } else {
          // we have the zone, create the charge packet
          tmpCP = new ChargePacket();
          tmpCP.zoneModel = CurrentRecord.ZoneModel;
          tmpCP.zoneResult = CurrentRecord.ZoneResult;
          tmpCP.priceGroup = CurrentRecord.ZoneResult;
          tmpCP.timeModel = "ALL";
          tmpCP.timeResult = "ANY";
          tmpCP.packetType = "I";
          tmpCP.rumName = "DUR";
          tmpCP.rumQuantity = CurrentRecord.Duration;
          tmpCP.ratePlanName = CurrentRecord.ZoneResult;
          tmpCP.service = "Voice";
          tmpCP.subscriptionID = "";
          CurrentRecord.addChargePacket(tmpCP);
        }
      }
    } catch (Exception e) {
      // error detected, add an error to the record
      tmpError = new RecordError("ERR_ZONE_LOOKUP_FAILURE", ErrorType.SPECIAL);
      CurrentRecord.addError(tmpError);
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
