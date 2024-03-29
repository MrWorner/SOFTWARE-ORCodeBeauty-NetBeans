package OR3Beauty;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 *  https://coderanch.com/t/339854/java/jTable-setBackground-Color-Row
 * @author MaximGodyna
 */

public class OR_PrintLnTableRenderer extends DefaultTableCellRenderer 
{
    public OR_PrintLnTableRenderer() 
    {
        super();
        setOpaque(true);
    } 
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) 
    { 
        if((boolean)table.getValueAt(row, 0))
        {
            setForeground(Color.black);        
            setBackground(Color.red);            
        }    
        else
        {    
            setBackground(Color.white);    
            setForeground(Color.black);    
        } 
        setText(value !=null ? value.toString() : "");
        return this;
    }
}
