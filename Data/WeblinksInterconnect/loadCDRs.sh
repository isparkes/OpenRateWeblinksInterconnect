cd InterconnectBilling
for i in cdrFromMSC*.out;
do
  echo loading $i
  cp $i cdrFromMSCICBilling
  mysqlimport --user=root --local --fields-terminated-by=';' --lines-terminated-by='\n' --verbose --debug wl1_0 cdrFromMSCICBilling
  mv $i $i.loaded
  rm cdrFromMSCICBilling
done
cd -
cd InterconnectReconciliation
for i in cdrFromMSC*.out;
do
  echo loading $i
  cp $i cdrFromMSCICReconciliation
  mysqlimport --user=root --local --fields-terminated-by=';' --lines-terminated-by='\n' --verbose --debug wl1_0 cdrFromMSCICReconciliation
  mv $i $i.loaded
  rm cdrFromMSCICReconciliation
done
cd -
