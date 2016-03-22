package com.sandy.capitalyst.domain.core;

import java.util.ArrayList ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import com.sandy.common.util.StringUtil ;

public abstract class AccountingItem {

    private String itemName = null ;
    private String parentPath = "" ;
    private AccountingItem parent = null ;
    private Account account = null ;
    
    private List<AccountingItem> derivedItems = new ArrayList<AccountingItem>() ;
    
    private Map<Date, Double> computedAmtMap = new HashMap<Date, Double>() ;
    
    public AccountingItem( String qualifiedName, Account operatingAccount ) {

        this.itemName = qualifiedName == null ? "" : qualifiedName ;
        this.account = operatingAccount ;
        
        int indexOfGt = this.itemName.lastIndexOf( ">" ) ;
        if( indexOfGt != -1 ) {
            this.itemName   = qualifiedName.substring( indexOfGt+1 ).trim() ;
            this.parentPath = qualifiedName.substring( 0, indexOfGt ).trim() ;
        }
    }
    
    String getParentPath() {
        return this.parentPath ;
    }
    
    void setParent( AccountingItem parent ) {
        this.parent = parent ;
    }
    
    public Account getAccount() {
        return this.account ;
    }
    
    public void setAccount( Account account ) {
        this.account = account ;
    }
    
    protected abstract double computeEntryForMonth( Date date ) ;
    
    public List<AccountingItem> getDerivedAccountingItems() {
        return derivedItems ;
    }
    
    public final double getEntryForMonth( Date date ) {
        if( computedAmtMap.containsKey( date ) ) {
            return computedAmtMap.get( date ) ;
        }
        
        double amt = computeEntryForMonth( date ) ;
        computedAmtMap.put( date, amt ) ;
        
        // We don't want piecewise definitions to start crediting and debiting
        // They are managed by a top level accounting item who will do the
        // debit and credit.
        if( account != null && StringUtil.isNotEmptyOrNull( itemName ) ) {
            account.operate( amt, date, this ) ;
        }
        
        return amt ;
    }
    
    public AccountingItem getParent() {
        return this.parent ;
    }
    
    public String getName() {
        return this.itemName ;
    }
}
