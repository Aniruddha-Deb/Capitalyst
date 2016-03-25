package com.sandy.capitalyst.domain.util.trigger;

import com.sandy.capitalyst.domain.core.Account ;
import com.sandy.capitalyst.domain.core.Account.Entry ;
import com.sandy.capitalyst.domain.core.AccountTrigger ;

public class NoConditionTrigger extends AccountTrigger {
    @Override
    public boolean isTriggered( Account account, Entry entry ) {
        return true ;
    }
}