
package com.sig.controller;

//import java.awt.List;
import com.sig.model.AllInvoicesTableModel;
import com.sig.model.AllLinesTableModel;
import com.sig.model.Invoice;
import com.sig.model.Line;
import com.sig.view.InvoiceDialog;
import com.sig.view.LineDialog;
import com.sig.view.SIGFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JFileChooser;

import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class Controller implements ActionListener, ListSelectionListener {
    
    private SIGFrame frame;
    private InvoiceDialog invoiceDialog;
    private LineDialog lineDialog;
    
    public Controller (SIGFrame frame)
    {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCmd = e.getActionCommand();
       System.out.println("Action: " + actionCmd);
       switch (actionCmd) {
           
           case "Load File":
               loadFile();
                 break;
               
               case "Save File":
                   saveFile();
                 break;
               
               case "Create New Item":
                   createNewItem();
                 break;
               
               case "Delete Item":
                   deleteItem();
                 break;
               
               case "Create New Invoice":
                   createNewInvoice();
                 break;
               
               case "Delete Invoice":
                   deleteInvoice();
                 break;
                 
               case "createNewInvoiceOK":
                   createNewInvoiceOK();
                 break;
                 
               case "createNewInvoiceCancel":
                   createNewInvoiceCancel();
                 break;
                 
               case "createNewLineOK":
                   createNewLineOK();
                 break;
                 
               case "createNewLineCancel":
                   createNewLineCancel();
                 break;
               
               
       }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedIndex = frame.getAllInvoicesTable().getSelectedRow();
        if (selectedIndex > -1)
        {
        System.out.println("you selcted rows: " + selectedIndex );
        Invoice currentInv = frame.getInvoices().get(selectedIndex);
        frame.getInvoiceNumLbl().setText(""+currentInv.getNum());
        frame.getInvoiceDateLbl().setText(currentInv.getInvoiceDate());
        frame.getCustomerNameLbl().setText (currentInv.getCustomerName());
        frame.getInvoiceTotalLbl().setText(""+currentInv.calculateInvoiceTotal());
        AllLinesTableModel allLinesTableModel = new AllLinesTableModel(currentInv.getInvoiceLines());
        frame.getInvoiceItemsTable().setModel(allLinesTableModel);
        allLinesTableModel.fireTableDataChanged();
        }
    }

    private void loadFile() {
       JFileChooser fc = new JFileChooser();
       try{
          int fileChooserResult = fc.showOpenDialog(frame); //opens the dialoug where user chooses the file
         // to load the file if user selects open
          if( fileChooserResult == JFileChooser.APPROVE_OPTION) {
              File headerFile = fc.getSelectedFile();
              Path headerPath = Paths.get(headerFile.getAbsolutePath()); //get the path of the file into object of path
            List<String> headerLines =  Files.readAllLines(headerPath); // read each invoice in one line 
            System.out.println("Invoices Files are read");
            // 1,22-11-2020,Ali
            ArrayList<Invoice> invoicesArr = new ArrayList<>(); //array list to buffer the data of all invoices into the table
            for (String headerLine: headerLines)
            {
                
                String [] headerComponents = headerLine.split(","); //to split each component of the invoice
                int invoiceNum = Integer.parseInt(headerComponents[0]);
                String invoiceDate = headerComponents[1];
                String invoiceCustomer = headerComponents[2];
                
                Invoice invoice = new Invoice(invoiceNum, invoiceDate, invoiceCustomer);
                invoicesArr.add(invoice);
            }
            System.out.println("check point");
            
            fileChooserResult = fc.showOpenDialog(frame);
            if(fileChooserResult==JFileChooser.APPROVE_OPTION)
            {
                File linesFile = fc.getSelectedFile();
                Path linesPath = Paths.get(linesFile.getAbsolutePath());
                List<String> linesLines =  Files.readAllLines(linesPath); 
               System.out.println("lines Files are read");
               
               for (String lineLine : linesLines)
               {
                   String [] lineComponents = lineLine.split(",");
                   
                   int invoiceNum = Integer.parseInt(lineComponents[0]);
                   String itemName = lineComponents[1];
                   double itemPrice = Double.parseDouble(lineComponents[2]);
                   int itemCount = Integer.parseInt(lineComponents[3]);
                  Invoice matchedInv = null; // the invoice with matched id in both files
                  
                  for(Invoice invoice : invoicesArr)
                  {
                      if (invoice.getNum() == invoiceNum)
                      {
                          matchedInv = invoice;
                          break;
                      }
                  }
                  Line line = new Line(matchedInv, itemName, itemPrice, itemCount);
                 matchedInv.getInvoiceLines().add(line);
               }
               System.out.println("check point");
            }
            frame.setInvoices(invoicesArr);
            AllInvoicesTableModel allInvoicesTableModel = new AllInvoicesTableModel (invoicesArr);
            frame.setAllInvoicesTableModel(allInvoicesTableModel); //insert the model inside the frame
            frame.getAllInvoicesTable().setModel(allInvoicesTableModel); //bring the invoices table fro  frame and set the model to : allInvoicesTableModel" to draw the table with the fields we need
            frame.getAllInvoicesTableModel().fireTableDataChanged(); //notifying the frame that we changed the table
          }
       } catch (IOException ex) {
           ex.printStackTrace();;
       }
    }

    private void saveFile() {
        ArrayList<Invoice> invoices = frame.getInvoices();
        String headers = "";
        String lines = "";
        for (Invoice invoice : invoices)
        {
            String invoiceCSV = invoice.getAsCSV();
            
            headers += invoiceCSV;
            headers += "\n";
            
            for (Line line : invoice.getInvoiceLines())
            {
                String linesCSV = line.getAsCSV();
                lines += linesCSV;
                lines += "\n";
            }
        }
        System.out.println("check point");
        
        try {
        JFileChooser fc = new JFileChooser();
        int result = fc.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION )
        {
            File headerFile = fc.getSelectedFile();
            FileWriter hfw = new FileWriter(headerFile);
            hfw.write(headers);
            hfw.flush();
            hfw.close();
            
            result = fc.showSaveDialog(frame);
            
            if(result == JFileChooser.APPROVE_OPTION)
            {
                File linesFile = fc.getSelectedFile();
                FileWriter lfw = new FileWriter(linesFile);
                lfw.write(lines);
                lfw.flush();
                lfw.close();
            }     
        }
        } catch (Exception ex)
        {
            
        }
        
        
    }

    private void createNewItem() {
     lineDialog = new LineDialog(frame);
     lineDialog.setVisible(true);
        
    }

    private void deleteItem() {
        int selectedInvoice = frame.getAllInvoicesTable().getSelectedRow();
        int selectedRow = frame.getInvoiceItemsTable().getSelectedRow();
        
        if (selectedInvoice > -1 && selectedRow > -1)
        {
            Invoice invoice = frame.getInvoices().get(selectedInvoice);
            invoice.getInvoiceLines().remove(selectedRow);
           
           AllLinesTableModel allLinesTableModel = new AllLinesTableModel (invoice.getInvoiceLines()); //to display the data after deletetion
           frame.getInvoiceItemsTable().setModel(allLinesTableModel);
           allLinesTableModel.fireTableDataChanged();
           frame.getAllInvoicesTableModel().fireTableDataChanged();
        }
        
        
    }

    private void createNewInvoice() {
        
        invoiceDialog = new InvoiceDialog(frame);
        invoiceDialog.setVisible(true);
        
    }

    private void deleteInvoice() {
        int selectedRow = frame.getAllInvoicesTable().getSelectedRow();
        if (selectedRow > -1)
        {
            frame.getInvoices().remove(selectedRow);
            frame.getAllInvoicesTableModel().fireTableDataChanged();
        }
    }

    private void createNewInvoiceOK() {
        String invoiceDate = invoiceDialog.getInvoiceDateField().getText();
      
        String customerName = invoiceDialog.getCustomerNameField().getText();
        int num = frame.getNextInvoiceNum();
        
        Invoice invoice = new Invoice(num, invoiceDate,customerName);
        frame.getInvoices().add(invoice);
        frame.getAllInvoicesTableModel().fireTableDataChanged();
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
    }

    private void createNewInvoiceCancel() {
        invoiceDialog.setVisible(false);
        invoiceDialog.dispose();
        invoiceDialog = null;
       
    }

    private void createNewLineOK() {
        String item = lineDialog.getItemNameField().getText();
        String countStr = lineDialog.getItemCountField().getText();
        String priceStr = lineDialog.getItemPriceField().getText();
        int counter = Integer.parseInt(countStr);
        double price = Double.parseDouble(priceStr);
        int selectedInvoice = frame.getAllInvoicesTable().getSelectedRow();
        
        if(selectedInvoice != -1)
        {
        Invoice invoice = frame.getInvoices().get(selectedInvoice);
        Line line = new Line(invoice, item, price, counter);
        invoice.getInvoiceLines().add(line);
        AllLinesTableModel allLinesTableModel = (AllLinesTableModel) frame.getInvoiceItemsTable().getModel();
        //allLinesTableModel.getLines().add(line);
        allLinesTableModel.fireTableDataChanged();
        frame.getAllInvoicesTableModel().fireTableDataChanged();
        }
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
        
     
    }

    private void createNewLineCancel() {
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }

}
