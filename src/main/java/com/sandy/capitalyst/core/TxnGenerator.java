package com.sandy.capitalyst.core;

import java.util.Date ;
import java.util.List ;

public interface TxnGenerator extends UniverseConstituent {
    
    public void getTransactionsForDate( Date date, List<Txn> txnList ) ;
}
