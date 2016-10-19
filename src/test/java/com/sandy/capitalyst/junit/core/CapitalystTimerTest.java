package com.sandy.capitalyst.junit.core;

import static com.sandy.capitalyst.util.Utils.parseDate ;
import static org.hamcrest.Matchers.equalTo ;
import static org.hamcrest.Matchers.is ;
import static org.junit.Assert.assertThat ;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.List ;

import org.junit.Before ;
import org.junit.Test ;

import com.sandy.capitalyst.clock.DayClock ;
import com.sandy.capitalyst.clock.DayObserver ;
import com.sandy.capitalyst.core.Universe ;

public class CapitalystTimerTest {

    private DayClock timer = null ;
    private class Observer implements DayObserver {
      
        private List<Date> dateList = new ArrayList<Date>() ;
        
        @Override
        public void handleDayEvent( Date date ) {
            dateList.add( date ) ;
        }
        
        public int numDateEventsReceived() {
            return dateList.size() ;
        }

        @Override
        public void setUniverse( Universe u ) {
        }

        @Override
        public Universe getUniverse() {
            return null ;
        }
    } ;
    
    @Before
    public void setUp() {
        DayClock.instance().reset() ;
    }
    
    @Test
    public void daysInJan2015() {
        
        Observer to1 = new Observer() ;
        Observer to2 = new Observer() ;
        
        timer = DayClock.instance() ;
        timer.registerTimeObserver( to1 ) ;
        timer.registerTimeObserver( to2 ) ;
        timer.setStartDate( parseDate( "01/01/2015" ) );
        timer.setEndDate( parseDate( "31/01/2015" ) );
        timer.run() ;
        
        assertThat( to1.numDateEventsReceived(), is( equalTo( 31 ) ) );
        assertThat( to2.numDateEventsReceived(), is( equalTo( 31 ) ) );
    }
    
    @Test
    public void daysIn2015() {
        
        Observer to1 = new Observer() ;
        
        timer = DayClock.instance() ;
        timer.registerTimeObserver( to1 ) ;
        timer.setStartDate( parseDate( "01/01/2015" ) );
        timer.setEndDate( parseDate( "31/01/2015" ) );
        timer.run() ;

        assertThat( to1.numDateEventsReceived(), is( equalTo( 365 ) ) );
    }
}
