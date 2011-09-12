package dk.frv.enav.ins.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import dk.frv.enav.ins.status.ComponentStatus;
import dk.frv.enav.ins.status.IStatusComponent;

public class StatusDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JLabel statusLbl;
	
	public StatusDialog() {
		super((Frame)null, "Status", true);
		
		setSize(300, 330);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		statusLbl = new JLabel();
		statusLbl.setVerticalAlignment(SwingConstants.TOP);
		
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();				
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(92)
							.addComponent(closeBtn, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(statusLbl, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(statusLbl, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(closeBtn)
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
	}
	
	public void showStatus(List<IStatusComponent> statusComponents) {
		StringBuilder buf = new StringBuilder();
		buf.append("<html>");
		for (IStatusComponent statusComponent : statusComponents) {
			ComponentStatus componentStatus = statusComponent.getStatus();			
			buf.append("<h4>" + componentStatus.getName() + "</h4>");
			buf.append("<p>");
			buf.append(componentStatus.getStatusHtml());			
			buf.append("</p>");
		}
		buf.append("</html>");
		
		statusLbl.setText(buf.toString());
		setVisible(true);
	}

}
