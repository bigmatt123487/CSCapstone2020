package main.java;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Reports extends JPanel implements Tile {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Controller control;
	private DefaultTableModel requestsModel;
	private String [][] requestsData; 
	private String requestColumns[];
	private JTable title_request_table;
	private final int titleColumn = 0;
	private JLabel title_lbl = new JLabel();
	private JLabel qty_lbl = new JLabel();
	private JLabel number_customers_lbl = new JLabel();
	String[][] orders;

	public Reports(Controller control) {
		this.control = control;
		setLayout(null);
		setBackground(Color.LIGHT_GRAY);
		
		/* Report Data */
		String data[][] = control.getReports();

		String da3[][]= control.getMonthlyBreakdown(); 
		
		for (String row[] : da3) {
			if (row[3].equals(0)) {
				row[3] = "False";
			} else {
				row[3] = "True";
			}
		}
		
		String titlesData[][] = control.getDistinctTitles();
		int rows = titlesData.length;
		String titlesData2[][]= new String[rows][4];
		String q1[][] = null;
		String q2[][] = null;
		int i = 0;
		int z = 0;
		int numCust = 0;
		for(i=0; i<rows; i++) {
			//System.out.println(titlesData[i][0]);
			q1 = control.getTitleDetails(titlesData[i][0]);
			if(q1.length > 0) {
				titlesData2[i][0] = q1[0][0];
				titlesData2[i][1] = q1[0][1];
				q2 = control.getQty(titlesData[i][0]);
				numCust += q2.length;
				int result=0;
				for(z=0; z<q2.length; z++) {
					result += Integer.parseInt(q2[z][0]);
				}
				titlesData2[i][2] = Integer.toString(result);
				titlesData2[i][3] = Integer.toString(q2.length);
			}
			
		}
		
		JTabbedPane reports_pane = new JTabbedPane(JTabbedPane.TOP);
		reports_pane.setBounds(10, 11, 973, 644);
		add(reports_pane);
		
		JPanel new_week_pulls = new JPanel();
		new_week_pulls.setForeground(Color.BLACK);
		new_week_pulls.setBorder(null);
		reports_pane.addTab("New Week Pulls", null, new_week_pulls, null);
		new_week_pulls.setLayout(null);
		
		JPanel monthly_breakdown = new JPanel();
		reports_pane.addTab("Monthly Breakdown", null, monthly_breakdown, null);
		
		/* New week pulls tab */
		
		
		// The two scrollpanes and two tables 
		JScrollPane new_titles_sp = new JScrollPane();
		new_titles_sp.setBounds(10, 87, 415, 507);
		new_week_pulls.add(new_titles_sp);
		
		String col[]={"Title","Issue #", "Qty", "# of Customers"};
		TableModel titleTablesModel = new DefaultTableModel(titlesData2, col) {
			private static final long serialVersionUID = 1L;
			
			public boolean isCellEditable(int row, int column) {
		      return false;
		    }
		 };
		JTable new_titles_table = new JTable(titleTablesModel);
		new_titles_table.setAutoCreateRowSorter(true);
		new_titles_sp.setViewportView(new_titles_table);
		
		JScrollPane titles_request_pane = new JScrollPane();
		titles_request_pane.setBounds(455, 225, 475, 369);
		new_week_pulls.add(titles_request_pane);
		
		requestColumns = new String[] {"Last Name","First Name","Qty"};
		requestsModel = new DefaultTableModel(requestsData, requestColumns) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		title_request_table = new JTable(requestsModel);
		title_request_table.setAutoCreateRowSorter(true);
		titles_request_pane.setViewportView(title_request_table);
		
		
		// The two export buttons for new week pulls 
		JButton export_flags_button = new JButton("Export New Week Flags");
		JButton export_request_button = new JButton("Export Request List");
		
		export_flags_button.setFont(new Font("Tahoma", Font.PLAIN, 14));
		export_flags_button.setBounds(315, 30, 177, 37);
		new_week_pulls.add(export_flags_button);
		export_request_button.setFont(new Font("Tahoma", Font.PLAIN, 14));
		export_request_button.setBounds(753, 177, 177, 37);
		new_week_pulls.add(export_request_button);
		
		// Labels for customer requests on new week pulls
		JLabel lblCustomerRequestsFor = new JLabel("Customer Requests for:");
		
		lblCustomerRequestsFor.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblCustomerRequestsFor.setBounds(455, 133, 176, 14);
		new_week_pulls.add(lblCustomerRequestsFor);
		title_lbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		title_lbl.setBounds(455, 158, 500, 14);
		new_week_pulls.add(title_lbl);
		qty_lbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		qty_lbl.setBounds(455, 181, 90, 14);
		new_week_pulls.add(qty_lbl);
		number_customers_lbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
		number_customers_lbl.setBounds(455, 204, 141, 14);
		new_week_pulls.add(number_customers_lbl);
		
		// These labels are used to show the corresponding value from the DB
		JLabel cur_flagged_lbl = new JLabel("1000");
		cur_flagged_lbl.setText(Integer.toString(rows));
		JLabel cur_flagged_cust_lbl = new JLabel("231");
		cur_flagged_cust_lbl.setText(Integer.toString(numCust));
		JLabel trigger_issue_lbl = new JLabel(control.getNumTitlesPendingIssue()[0][0]);
		JLabel no_requests_lbl = new JLabel("14");
		int amount = Integer.parseInt(control.getNumTitles()[0][0]) - rows;
		no_requests_lbl.setText(Integer.toString(amount));

		cur_flagged_lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		cur_flagged_lbl.setBounds(10, 10, 47, 14);
		new_week_pulls.add(cur_flagged_lbl);
		cur_flagged_cust_lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		cur_flagged_cust_lbl.setBounds(10, 30, 47, 14);
		new_week_pulls.add(cur_flagged_cust_lbl);
		trigger_issue_lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		trigger_issue_lbl.setBounds(10, 50, 47, 14);
		new_week_pulls.add(trigger_issue_lbl);
		no_requests_lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		no_requests_lbl.setBounds(10, 70, 47, 14);
		new_week_pulls.add(no_requests_lbl);

		// Labels for the sentences/strings on new week pulls
		JLabel lbl1 = new JLabel("Titles Currently Flagged");
		JLabel lbl2 = new JLabel("Customers");
		JLabel lbl3 = new JLabel("Titles have triggered Issue #'s");
		JLabel lbl4 = new JLabel("Titles Have No Customer Requests");

		lbl1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl1.setBounds(58, 10, 176, 14);
		new_week_pulls.add(lbl1);
		lbl2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl2.setBounds(58, 30, 176, 14);
		new_week_pulls.add(lbl2);
		lbl3.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl3.setBounds(58, 50, 200, 14);
		new_week_pulls.add(lbl3);
		lbl4.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lbl4.setBounds(58, 70, 239, 14);
		new_week_pulls.add(lbl4);
				
		/* End of new week pulls */
		
		
		/* monthly breakdown */
		
		monthly_breakdown.setLayout(null);
		JScrollPane breakdown_pane = new JScrollPane();
		breakdown_pane.setBounds(10, 11, 443, 594);
		monthly_breakdown.add(breakdown_pane);
		
		DefaultTableModel monthlyBreakdownModel = new DefaultTableModel(da3, new String[] {"Title","Qty","Pending Issue #", "Flagged in last 6 months"}) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JTable breakdown_table = new JTable(monthlyBreakdownModel);
		breakdown_table.setAutoCreateRowSorter(true);
		breakdown_pane.setViewportView(breakdown_table);

		
		
		// These are the labels that hold the value of the given request on monthly breakdown 
		JLabel no_titles_lbl = new JLabel(control.getNumTitles()[0][0].toString());
		JLabel no_customers_lbl = new JLabel(control.getNumCustomers()[0][0].toString());
		JLabel no_special_lbl = new JLabel(control.getNumSpecialComments()[0][0].toString());
		
		JLabel no_flagged_lbl = new JLabel("77");
		JLabel no_zeroRequest_lbl = new JLabel("23");
		
		//Font, bounds, and adding lbl's to the panel
		no_titles_lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		no_titles_lbl.setBounds(471, 175, 55, 14);
		monthly_breakdown.add(no_titles_lbl);
		no_customers_lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		no_customers_lbl.setBounds(471, 210, 55, 14);
		monthly_breakdown.add(no_customers_lbl);
		no_special_lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		no_special_lbl.setBounds(471, 245, 55, 14);
		monthly_breakdown.add(no_special_lbl);
		
		no_flagged_lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		no_flagged_lbl.setBounds(471, 315, 55, 14);
		monthly_breakdown.add(no_flagged_lbl);
		no_zeroRequest_lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		no_zeroRequest_lbl.setBounds(471, 350, 55, 14);
		monthly_breakdown.add(no_zeroRequest_lbl);
		
		
		// These are the labels of text on the monthly breakdown screen, not the amount value 
		JLabel titles_lbl = new JLabel("Titles");
		JLabel customers_lbl = new JLabel("Customers");
		JLabel special_lbl = new JLabel("Special Order Notes");
		JLabel flagged_lbl = new JLabel("Titles not flagged in over 6 months");
		JLabel zero_Requests_lbl = new JLabel("Titles have 0 Customer Requests");
		JLabel breakdown_lbl = new JLabel("Database currently has:");
		
		breakdown_lbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		breakdown_lbl.setBounds(463, 144, 176, 14);
		monthly_breakdown.add(breakdown_lbl);
		titles_lbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		titles_lbl.setBounds(536, 175, 176, 14);
		monthly_breakdown.add(titles_lbl);
		customers_lbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		customers_lbl.setBounds(536, 210, 176, 14);
		monthly_breakdown.add(customers_lbl);
		special_lbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		special_lbl.setBounds(536, 245, 176, 14);
		monthly_breakdown.add(special_lbl);

		flagged_lbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		flagged_lbl.setBounds(536, 315, 250, 14);
		monthly_breakdown.add(flagged_lbl);
		zero_Requests_lbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		zero_Requests_lbl.setBounds(536, 350, 232, 14);
		monthly_breakdown.add(zero_Requests_lbl);
		
		// These are the 5 different export buttons for the monthly breakdown screen 
		JButton export_titles_btn = new JButton("Export");
		JButton export_customers_btn = new JButton("Export");
		JButton export_flagged_btn = new JButton("Export");
		JButton export_special_comments_btn = new JButton("Export");
		JButton export_zeroRequests_btn = new JButton("Export");
		
		// Font, bounds, and adding buttons to panel
		export_titles_btn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		export_titles_btn.setBounds(869, 173, 89, 23);
		monthly_breakdown.add(export_titles_btn);
		export_customers_btn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		export_customers_btn.setBounds(869, 208, 89, 23);
		monthly_breakdown.add(export_customers_btn);
		export_flagged_btn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		export_flagged_btn.setBounds(869, 313, 89, 23);
		monthly_breakdown.add(export_flagged_btn);
		export_special_comments_btn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		export_special_comments_btn.setBounds(869, 243, 89, 23);
		monthly_breakdown.add(export_special_comments_btn);
		

		export_zeroRequests_btn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		export_zeroRequests_btn.setBounds(869, 348, 89, 23);
		monthly_breakdown.add(export_zeroRequests_btn);


		JRadioButton csv = new JRadioButton("CSV");
		JRadioButton pdf = new JRadioButton("PDF");
		csv.setBounds(869, 383, 89, 23);
		pdf.setBounds(869, 418, 89, 23);
		ButtonGroup bG = new ButtonGroup();
		bG.add(csv);
		bG.add(pdf);
		csv.setSelected(true);
		monthly_breakdown.add(csv);
		monthly_breakdown.add(pdf);


		/* End of monthly breakdown */

		new_titles_table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String title = new_titles_table.getValueAt(new_titles_table.getSelectedRow(), titleColumn).toString();
				orders = control.getOrdersByTitle(title);
				requestsModel.setDataVector(orders, requestColumns);
				int quantity = 0;
				
				for (String[] order : orders) {
					quantity += Integer.parseInt(order[2]);
				}
				
				title_lbl.setText(title);
				qty_lbl.setText(String.format("Total Qty: %d", quantity));
				number_customers_lbl.setText(String.format("# of Customers: %d", orders.length));
			}
		});
		
		
		export_request_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (requestsModel.getRowCount() > 0) {

					String temp = title_lbl.getText();
					if (temp.length() > 35) {
						temp = temp.substring(0, 35);
					}

					String filePath = control.saveFile(new_week_pulls, temp, ".xlsx"); // Calls saveFile() in controller.// Essentially opens file saver and gets

					if (filePath != null) {
						String columns[] = {"Last Name", "First Name", "Qty"}; // Columns for your excel sheet
						control.exportXLSX(orders, null, filePath, "Single Title Order List", columns);
					}
				}
			}
		});
		export_customers_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (csv.isSelected()) {
					//CSV
					String filePath = control.saveFile(monthly_breakdown, "Customers", ".xlsx");  // Calls saveFile() in controller. Essentially opens file saver and gets save destination.
					if (filePath != null) {
						String columns[] = {"Last Name", "First Name", "Phone #1", "Email"};        // Columns for your excel sheet
						String query = "SELECT [Last Name], [First Name], [Phone #1], [Email] FROM [newDLC].[dbo].[Customer]"; // Query matching the columns
						control.exportXLSX(null, query, filePath, "Customers", columns);            // Calling export. Check the function for param values
					}
				} else {
					//PDF

					Document pdfDoc = new Document();
					//Create new File
					String filePath = control.saveFile(monthly_breakdown, "Monthly_Titles_List", ".pdf");
					File file = new File(filePath);

					try {
						file.createNewFile();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					FileOutputStream fop = null;
					try {
						fop = new FileOutputStream(file);
					} catch (FileNotFoundException fileNotFoundException) {
						fileNotFoundException.printStackTrace();
					}
					try {
						PdfWriter.getInstance(pdfDoc, fop);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					pdfDoc.open();

					String[] header = new String[]{"Last Name", "First Name", "Phone #1", "Email"};
					String query = "SELECT [Last Name], [First Name], [Phone #1], [Email] FROM [newDLC].[dbo].[Customer]";
					String[][] body = control.select(query);

					//Table for header
					PdfPTable pdfTableHeader = new PdfPTable(header.length);
					for (int j = 0; j < header.length; j++) {
						Phrase frase = new Phrase(header[j]);
						PdfPCell cell = new PdfPCell(frase);
						cell.setBackgroundColor(new BaseColor(Color.lightGray.getRGB()));
						pdfTableHeader.addCell(cell);
					}

					try {
						pdfTableHeader.setWidths(new float[]{2, 2, 2, 4});
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}

					try {
						pdfDoc.add(pdfTableHeader);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					//Table for body
					PdfPTable pdfTable = new PdfPTable(header.length);
					for (int i = 0; i < body.length; i++) {
						for (int j = 0; j < body[i].length; j++) {
							pdfTable.addCell(new Phrase(body[i][j]));
						}
					}

					try {
						pdfTable.setWidths(new float[]{2, 2, 2, 4});
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}

					try {
						pdfDoc.add(pdfTable);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					pdfDoc.close();
					try {
						fop.flush();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					try {
						fop.close();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
			}
			
		});
		
		
		//Monthly Title
		export_titles_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				if (csv.isSelected()) {
					//CSV

					String filePath = control.saveFile(monthly_breakdown, "Monthly Titles", ".xlsx");
					if (filePath != null) {
						String columns[] = {"Title", "Customer", "Quantity"};
						String query = "SELECT [Title], [Customer Code], [Quantity] FROM [newDLC].[dbo].[Order]";
						control.exportXLSX(null, query, filePath, "Titles", columns);
					}
				} else {
					//PDF

					Document pdfDoc = new Document();
					//Create new File
					String filePath = control.saveFile(monthly_breakdown, "Monthly_Titles_List", ".pdf");
					File file = new File(filePath);

					try {
						file.createNewFile();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					FileOutputStream fop = null;
					try {
						fop = new FileOutputStream(file);
					} catch (FileNotFoundException fileNotFoundException) {
						fileNotFoundException.printStackTrace();
					}
					try {
						PdfWriter.getInstance(pdfDoc, fop);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					pdfDoc.open();

					String[] header = new String[]{"Title", "Customer", "Quantity"};
					String query = "SELECT [Title], [Customer Code], [Quantity] FROM [newDLC].[dbo].[Order]";
					String[][] body = control.select(query);

					//Table for header
					PdfPTable pdfTableHeader = new PdfPTable(header.length);
					for (int j = 0; j < header.length; j++) {
						Phrase frase = new Phrase(header[j]);
						PdfPCell cell = new PdfPCell(frase);
						cell.setBackgroundColor(new BaseColor(Color.lightGray.getRGB()));
						pdfTableHeader.addCell(cell);
					}

					try {
						pdfTableHeader.setWidths(new float[]{5, 2, 1});
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}

					try {
						pdfDoc.add(pdfTableHeader);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					//Table for body
					PdfPTable pdfTable = new PdfPTable(header.length);
					for (int i = 0; i < body.length; i++) {
						for (int j = 0; j < body[i].length; j++) {
							pdfTable.addCell(new Phrase(body[i][j]));
						}
					}

					try {
						pdfTable.setWidths(new float[]{5, 2, 1});
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}

					try {
						pdfDoc.add(pdfTable);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					pdfDoc.close();
					try {
						fop.flush();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					try {
						fop.close();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
            }
        });
		
		// not flagged in 6 months button export
		export_flagged_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				if (csv.isSelected()) {
					//CSV

					String filePath = control.saveFile(monthly_breakdown, "Titles not Flagged 6mo+", ".xlsx");
					if (filePath != null) {
						String columns[] = {"Catalog ID", "Distributor", "Title", "Flag", "Release"};
						String query = "SELECT [Catalog ID], [Distributor], [Series], [Flag], [Release] FROM [newDLC].[dbo].[Catalog] WHERE [Flag] < 1 AND [Release] <= dateadd(month, -6, getdate())";

						control.exportXLSX(null, query, filePath, "Catalog", columns);
					}
				} else {
					//PDF

					Document pdfDoc = new Document();
					//Create new File
					String filePath = control.saveFile(monthly_breakdown, "Monthly_Titles_List", ".pdf");
					File file = new File(filePath);

					try {
						file.createNewFile();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					FileOutputStream fop = null;
					try {
						fop = new FileOutputStream(file);
					} catch (FileNotFoundException fileNotFoundException) {
						fileNotFoundException.printStackTrace();
					}
					try {
						PdfWriter.getInstance(pdfDoc, fop);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					pdfDoc.open();

					String[] header = new String[]{"Catalog ID", "Distributor", "Title", "Flag", "Release"};
					String query = "SELECT [Catalog ID], [Distributor], [Series], [Flag], [Release] FROM [newDLC].[dbo].[Catalog] WHERE [Flag] < 1 AND [Release] <= dateadd(month, -6, getdate())";
					String[][] body = control.select(query);

					//Table for header
					PdfPTable pdfTableHeader = new PdfPTable(header.length);
					for (int j = 0; j < header.length; j++) {
						Phrase frase = new Phrase(header[j]);
						PdfPCell cell = new PdfPCell(frase);
						cell.setBackgroundColor(new BaseColor(Color.lightGray.getRGB()));
						pdfTableHeader.addCell(cell);
					}

					try {
						pdfTableHeader.setWidths(new float[]{2, 3, 4, 1, 1});
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}

					try {
						pdfDoc.add(pdfTableHeader);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					//Table for body
					PdfPTable pdfTable = new PdfPTable(header.length);
					for (int i = 0; i < body.length; i++) {
						for (int j = 0; j < body[i].length; j++) {
							pdfTable.addCell(new Phrase(body[i][j]));
						}
					}

					try {
						pdfTable.setWidths(new float[]{2, 3, 4, 1, 1});
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}

					try {
						pdfDoc.add(pdfTable);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					pdfDoc.close();
					try {
						fop.flush();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					try {
						fop.close();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
            }
        }); 
		
		export_special_comments_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					if (csv.isSelected()) {
						//CSV
						String filePath = control.saveFile(monthly_breakdown, "Special_Comments", ".xlsx");
						if (filePath != null) {
							String columns[] = {"Title", "Comments", "Issue Start", "Issue End"};
							String query = "SELECT [Title], [Comments], [Issue Start], [Issue End] FROM [newDLC].[dbo].[Order] WHERE [Comments] != ''";
							control.exportXLSX(null, query, filePath, "Titles with Comments", columns);
						}
					} else {
						//PDF

						Document pdfDoc = new Document();
						//Create new File
						String filePath = control.saveFile(monthly_breakdown, "Special_Comments", ".pdf");
						File file = new File(filePath);

						try {
							file.createNewFile();
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}
						FileOutputStream fop = null;
						try {
							fop = new FileOutputStream(file);
						} catch (FileNotFoundException fileNotFoundException) {
							fileNotFoundException.printStackTrace();
						}
						try {
							PdfWriter.getInstance(pdfDoc, fop);
						} catch (DocumentException documentException) {
							documentException.printStackTrace();
						}
						pdfDoc.open();

						String[] header = new String[]{"Title", "Comments", "Issue Start", "Issue End"};
						String query = "SELECT [Title], [Comments], [Issue Start], [Issue End] FROM [newDLC].[dbo].[Order] WHERE [Comments] != ''";
						String[][] body = control.select(query);

						//Table for header
						PdfPTable pdfTableHeader = new PdfPTable(header.length);
						for (int j = 0; j < header.length; j++) {
							Phrase frase = new Phrase(header[j]);
							PdfPCell cell = new PdfPCell(frase);
							cell.setBackgroundColor(new BaseColor(Color.lightGray.getRGB()));
							pdfTableHeader.addCell(cell);
						}

						try {
							pdfTableHeader.setWidths(new float[]{1, 4, 1, 1});
						} catch (DocumentException documentException) {
							documentException.printStackTrace();
						}

						try {
							pdfDoc.add(pdfTableHeader);
						} catch (DocumentException documentException) {
							documentException.printStackTrace();
						}
						//Table for body
						PdfPTable pdfTable = new PdfPTable(header.length);
						for (int i = 0; i < body.length; i++) {
							for (int j = 0; j < body[i].length; j++) {
								pdfTable.addCell(new Phrase(body[i][j]));
							}
						}

						try {
							pdfTable.setWidths(new float[]{1, 4, 1, 1});
						} catch (DocumentException documentException) {
							documentException.printStackTrace();
						}

						try {
							pdfDoc.add(pdfTable);
						} catch (DocumentException documentException) {
							documentException.printStackTrace();
						}
						pdfDoc.close();
						try {
							fop.flush();
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}
						try {
							fop.close();
						} catch (IOException ioException) {
							ioException.printStackTrace();
						}
					}
	            }
			});
		

		// Zero request skeleton
		export_zeroRequests_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				if (csv.isSelected()) {
					//CSV
					String filePath = control.saveFile(monthly_breakdown, "Titles with 0 Requests", ".xlsx");
					if (filePath != null) {
						String columns[] = {"Store Code", "Customer Code", "Title"};
						String query = "SELECT [Store Code], [Customer Code], [Title] FROM [newDLC].[dbo].[Order] WHERE [Quantity] < 1";
						control.exportXLSX(null, query, filePath, "Order", columns);
					}
				} else {
					//PDF

					Document pdfDoc = new Document();
					//Create new File
					String filePath = control.saveFile(monthly_breakdown, "Monthly_Titles_List", ".pdf");
					File file = new File(filePath);

					try {
						file.createNewFile();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					FileOutputStream fop = null;
					try {
						fop = new FileOutputStream(file);
					} catch (FileNotFoundException fileNotFoundException) {
						fileNotFoundException.printStackTrace();
					}
					try {
						PdfWriter.getInstance(pdfDoc, fop);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					pdfDoc.open();

					String[] header = new String[]{"Store Code", "Customer Code", "Title"};
					String query = "SELECT [Store Code], [Customer Code], [Title] FROM [newDLC].[dbo].[Order] WHERE [Quantity] < 1";
					String[][] body = control.select(query);

					//Table for header
					PdfPTable pdfTableHeader = new PdfPTable(header.length);
					for (int j = 0; j < header.length; j++) {
						Phrase frase = new Phrase(header[j]);
						PdfPCell cell = new PdfPCell(frase);
						cell.setBackgroundColor(new BaseColor(Color.lightGray.getRGB()));
						pdfTableHeader.addCell(cell);
					}

					try {
						pdfTableHeader.setWidths(new float[]{1, 1, 4});
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}

					try {
						pdfDoc.add(pdfTableHeader);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					//Table for body
					PdfPTable pdfTable = new PdfPTable(header.length);
					for (int i = 0; i < body.length; i++) {
						for (int j = 0; j < body[i].length; j++) {
							pdfTable.addCell(new Phrase(body[i][j]));
						}
					}

					try {
						pdfTable.setWidths(new float[]{1, 1, 4});
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}

					try {
						pdfDoc.add(pdfTable);
					} catch (DocumentException documentException) {
						documentException.printStackTrace();
					}
					pdfDoc.close();
					try {
						fop.flush();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
					try {
						fop.close();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
            }
        }); 
		
		
	}
	
}
