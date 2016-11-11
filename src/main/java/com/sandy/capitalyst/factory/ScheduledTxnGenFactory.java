package com.sandy.capitalyst.factory;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.InputStream ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.apache.poi.ss.usermodel.Cell ;
import org.apache.poi.ss.usermodel.Row ;
import org.apache.poi.ss.usermodel.Sheet ;
import org.apache.poi.ss.usermodel.Workbook ;
import org.apache.poi.xssf.usermodel.XSSFWorkbook ;

import com.sandy.capitalyst.cfg.Cfg ;
import com.sandy.capitalyst.core.Universe ;
import com.sandy.capitalyst.txgen.ScheduledTxnDef ;
import com.sandy.capitalyst.txgen.ScheduledTxnGen ;
import com.sandy.capitalyst.util.converter.ScheduleTxnDefConverter ;
import com.sandy.common.util.StringUtil ;

public class ScheduledTxnGenFactory extends Factory {

    static final Logger log = Logger.getLogger( ScheduledTxnGenFactory.class ) ;
    
    private String id = null ;
    private Universe universe = null ;
    private ScheduleTxnDefConverter converter = new ScheduleTxnDefConverter() ;
    
    private ScheduledTxnDef[] txnDefs = null ;
    
    @Cfg( mandatory=false )
    private String defaultCreditAccount = null ;
    
    @Cfg( mandatory=false )
    private String defaultDebitAccount = null ;
    
    @Cfg private File definitionFile = null ;
    
    public String getDefaultCreditAccount() {
        return defaultCreditAccount ;
    }

    public void setDefaultCreditAccount( String defaultCreditAccount ) {
        this.defaultCreditAccount = defaultCreditAccount ;
    }

    public String getDefaultDebitAccount() {
        return defaultDebitAccount ;
    }

    public void setDefaultDebitAccount( String defaultDebitAccount ) {
        this.defaultDebitAccount = defaultDebitAccount ;
    }
    
    public File getDefinitionFile() {
        return definitionFile ;
    }

    public void setDefinitionFile( File definitionFile ) {
        this.definitionFile = definitionFile ;
    }
    
    public void setId( String id ) {
        this.id = id ;
    }
    
    public String getId() {
        return this.id ;
    }
    
    public void setUniverse( Universe u ) {
        this.universe = u ;
    }
    
    public Universe getUniverse() {
        return this.universe ;
    }

    @Override
    public void initializePostConfig() {
        if( txnDefs == null && definitionFile == null ) {
            throw new IllegalStateException( "Both transaction definitions and "
                    + "definition file is null" ) ;
        }
        
        if( definitionFile != null ) {
            if( !definitionFile.exists() ) {
                throw new IllegalArgumentException( "Definition file does not exist" ) ;
            }
            try {
                loadTxnDefsFromFile() ;
                for( ScheduledTxnDef def : txnDefs ) {
                    universe.registerTxnGenerator( new ScheduledTxnGen( def ) ) ;
                }
            }
            catch( Exception e ) {
                throw new IllegalStateException( "Invalid definition file", e ) ;
            }
        }
    }
    
    private void loadTxnDefsFromFile() throws Exception {
        
        InputStream is = new FileInputStream( definitionFile ) ;
        Workbook workbook = new XSSFWorkbook( is ) ;
        
        Sheet sheet = workbook.getSheetAt( 0 ) ;
        int numLines = sheet.getLastRowNum() - sheet.getFirstRowNum() ;
        
        List<ScheduledTxnDef> defs = new ArrayList<ScheduledTxnDef>() ;
        
        for( int rowNum=0; rowNum<=numLines; rowNum++ ) {
            Row row = sheet.getRow( rowNum ) ;
            if( isIgnorableRow( row ) ) continue ;
            
            StringBuilder buffer = new StringBuilder() ;
            
            for( int colNum=0; colNum<ScheduleTxnDefConverter.NUM_COLS; colNum++ ) {
                String cellContent = "" ;
                Cell cell = row.getCell( colNum ) ;
            
                if( cell != null ) {
                    cellContent = cell.toString() ;
                }
                
                cellContent = getInterpolatedValue( cellContent ) ;
                buffer.append( cellContent ).append( ":" ) ;
            }
            buffer.append( "EOR" ) ;
            defs.add( createTxnDef( buffer.toString() ) ) ;
        }
        
        txnDefs = defs.toArray( new ScheduledTxnDef[1] ) ;
    }
    
    private boolean isIgnorableRow( Row row ) {
        
        if( row != null ) {
            Cell cell1 = row.getCell( 0 ) ;
            Cell cell2 = row.getCell( 1 ) ;
            
            if( cell1 != null ) {
                String content = cell1.toString() ;
                if( content.startsWith( "#" ) || 
                    content.startsWith( "Classifiers" ) ) {
                    return true ;
                }
            }
            
            if( cell2 != null ) {
                String content = cell2.toString() ;
                if( StringUtil.isNotEmptyOrNull( content ) ) {
                    return false ;
                }
            }
        }
        return true ;
    }
    
    private ScheduledTxnDef createTxnDef( String input ) {
        
        log.debug( "\tTxn def = " + input ) ;
        ScheduledTxnDef txnDef = converter.createTxnDef( input ) ;
        
        if( txnDef.getDebitACNo() == null ) {
            txnDef.setDebitACNo( getDefaultDebitAccount() ) ;
        }
        
        if( txnDef.getCreditACNo() == null ) {
            txnDef.setCreditACNo( getDefaultCreditAccount() ) ;
        }
        
        return txnDef ;
    }
    
    private String getInterpolatedValue( String input ) {
        return getUniverse().getConfiguration().interpolate( input ) ;
    }
}