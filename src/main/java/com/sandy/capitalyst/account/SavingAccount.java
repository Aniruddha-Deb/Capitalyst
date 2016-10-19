package com.sandy.capitalyst.account;

import java.util.Date ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.clock.EndOfDayObserver ;
import com.sandy.capitalyst.clock.EndOfQuarterObserver ;
import com.sandy.capitalyst.core.Txn ;

public class SavingAccount extends BankAccount 
    implements EndOfDayObserver, EndOfQuarterObserver {
    
    private double accumulatedInterest  = 0 ;
    
    @Cfg 
    private double roi = 0 ;
    
    public void setRoi( double roi ) {
        this.roi = roi ;
    }
    
    @Override
    public void handleEndOfDayEvent( Date date ) {

        double dailyInterest = getAmount() * (roi/(100*365)) ;
        accumulatedInterest += dailyInterest ;
    }

    @Override
    public void handleEndOfQuarterEvent( Date date ) {
        
        if( accumulatedInterest > 0 ) {
            Txn txn = new Txn( getAccountNumber(), accumulatedInterest, date ) ;
            txn.setDescription( "SB Interest for quarter" ) ;
            
            super.getUniverse().postTransaction( txn ) ;
            accumulatedInterest = 0 ;
        }
    }
}
