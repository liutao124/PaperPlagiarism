/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cn.octopus.paperplagiarism.vo;

import javax.swing.JTable;

/**
 *
 * @author liutao
 */
public class PageTableModel{ 
    
    public static JTable create(JTable dataTable, int numSize){
        Object[][] initValue = new Object[numSize][3]; 
        for(int i=0; i<numSize; i++){
            for(int j=0; j<3; j++){
                initValue[i][j] = null;
            }
        }
        
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            initValue, new String [] { "源文件", "对比文件", "重复率" }
        ) {
            final Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            final boolean[] canEdit = new boolean [] {
                false, false, false
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dataTable.setRowHeight(25);
        dataTable.setShowGrid(true);

        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dataTable.getColumnModel().getColumn(0).setPreferredWidth(459);
        dataTable.getColumnModel().getColumn(1).setPreferredWidth(459);
        dataTable.getColumnModel().getColumn(2).setPreferredWidth(82);
        
        return dataTable;
    }
}