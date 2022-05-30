
package com.sig.view;

import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class LineDialog extends JDialog {
    private JLabel itemCountLabel;
    private  JLabel itemPriceLabel;
    private JLabel itemNameLabel;
    private JTextField itemCountField;
    private JTextField itemPriceField;
    private JTextField itemNameField;
    private JButton okButton;
    private JButton cancelButton;

    public LineDialog(SIGFrame f) {
        itemNameField = new JTextField(20);
        itemNameLabel = new JLabel("Item Name: ");
        
        itemCountField = new JTextField(20);
        itemCountLabel = new JLabel("Item Count: ");
        
        itemPriceField = new JTextField(20);
        itemPriceLabel = new JLabel("item Price: ");
        
        
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
       
        okButton.setActionCommand("createNewLineOK");
        cancelButton.setActionCommand("createNewLineCancel");
        okButton.addActionListener(f.getController());
        cancelButton.addActionListener(f.getController());
        
        setLayout(new GridLayout(4,2));
        
        add(itemNameLabel);
        add(itemNameField);
        
        add(itemCountLabel);
        add(itemCountField);
        
        add(itemPriceLabel);
        add(itemPriceField);
       
        add(okButton);
        add(cancelButton);
        pack();
    }

    public JTextField getItemCountField() {
        return itemCountField;
    }

    public JTextField getItemPriceField() {
        return itemPriceField;
    }

    public JTextField getItemNameField() {
        return itemNameField;
    }

  
}
