package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.clustering.main.Cluster;
import com.clustering.main.DriverClass;
import com.clustering.visualization.Dendrogram;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {
	public Button beginButton;
	public Button clusterButton;
	public RadioButton kmedoids;
	public RadioButton hierarchical;
	public RadioButton buckshot;
	public RadioButton mbkm;
	public TextField kTextField;
	public TextField thresholdTextField;
	public ComboBox<String> linkComboBox;
	public Dendrogram dp;
	public JFrame frame;
	public Button connectToDBButton;
	public Button extractXMLButton;
	public Text linkText;
	public Text kText;
	public Text thresholdText;
	public boolean agglomerativeFlag;
	public static ArrayList<String> sequences;
	public TextArea sequencesTextArea;

	public void handleBeginButton() throws IOException {

		Stage stage;
		Parent root;
		stage = (Stage) beginButton.getScene().getWindow();

		root = FXMLLoader.load(getClass().getResource("clustering.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("DNA Analyzer");
		stage.show();
	}

	public void handleClusterButton() throws Exception {
		if (sequencesTextArea.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid DNA Sequences");
			alert.setHeaderText(null);
			alert.setContentText("No DNA Sequences found");
			alert.showAndWait();
			throw new Exception ("No DNA sequences");
		} else {
			Scanner scan = new Scanner (sequencesTextArea.getText());
			scan.useDelimiter(" ");
			sequences = new ArrayList<String> ();
			while(scan.hasNext()) {
				String temp = scan.next();
				temp.replaceAll("\\s+","");
				if (temp.length() > 0) {
					sequences.add(temp);
				}
			}
		}
		int linkType = 3;
		double threshold = 0.0;
		int k = 1;
		String link = "Average Link";
		if (agglomerativeFlag) {
			try {
				link = linkComboBox.getValue();
			} catch (Exception e) {
				link = "Average Link";
			}
			
			try {
				threshold = Double.parseDouble(thresholdTextField.getText());
				System.out.println(threshold);
			} catch (Exception e) {
				threshold = 0.0;
			}
			
			try {
				if (link.equals("Single Link"))
					linkType = 1;
				else if (link.equals("Complete Link"))
					linkType = 2;
				else if (link.equals("Average Link"))
					linkType = 3;
				else if (link.equals("Centroid Link"))
					linkType = 4;
			} catch (Exception e) {
				linkType = 3;
			}
			
		} else {
			try {
				k = Integer.parseInt(kTextField.getText());
			} catch (Exception e) {
				k = 1;
			}
		}

		if (kmedoids.isSelected()) {
			try {
				ArrayList<Cluster> newClusters = DriverClass.doKMeans(sequences, k);
				saveToDB(newClusters);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		if (hierarchical.isSelected()) {
			try {
				DriverClass.doHierarchical(sequences, linkType, threshold);
				Stage stage = new Stage();
				Pane root = new Pane();
				final SwingNode swingNode = new SwingNode();
				createAndSetSwingContent(swingNode);
				root.getChildren().add(swingNode);
				Scene scene = new Scene(root);
				stage.setScene(scene);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		
		if (buckshot.isSelected()) {
			try {
				ArrayList<Cluster> newClusters = DriverClass.doBuckshot(sequences, k);
				Stage stage = new Stage();
				Pane root = new Pane();
				final SwingNode swingNode = new SwingNode();
				createAndSetSwingContent(swingNode);
				root.getChildren().add(swingNode);
				Scene scene = new Scene(root);
				stage.setScene(scene);
				saveToDB(newClusters);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (mbkm.isSelected()) {
			try {
				ArrayList<Cluster> newClusters = DriverClass.doMBKM(sequences, k);
				saveToDB(newClusters);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void handleComboBox() throws IOException {

	}

	public void handleRadioKM() throws IOException {
		kText.setVisible(true);
		kTextField.setVisible(true);
		thresholdText.setVisible(false);
		thresholdTextField.setVisible(false);
		linkComboBox.setVisible(false);
		linkText.setVisible(false);
		agglomerativeFlag = false;
	}

	public void handleRadioAH() throws IOException {
		kText.setVisible(false);
		kTextField.setVisible(false);
		thresholdText.setVisible(true);
		thresholdTextField.setVisible(true);
		linkComboBox.setVisible(true);
		linkText.setVisible(true);
		agglomerativeFlag = true;
	}

	public void handleRadioBS() throws IOException {
		kText.setVisible(true);
		kTextField.setVisible(true);
		thresholdText.setVisible(false);
		thresholdTextField.setVisible(false);
		linkComboBox.setVisible(false);
		linkText.setVisible(false);
		agglomerativeFlag = false;
	}
	
	public void handleRadioMB() throws IOException {
		kText.setVisible(true);
		kTextField.setVisible(true);
		thresholdText.setVisible(false);
		thresholdTextField.setVisible(false);
		linkComboBox.setVisible(false);
		linkText.setVisible(false);
		agglomerativeFlag = false;
	}

	private void createAndSetSwingContent(final SwingNode swingNode) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new JFrame();
				frame.setSize(400, 300);
				frame.setLocation(400, 300);
				JPanel content = new JPanel();
				Dendrogram dp = new Dendrogram();
				dp.setRoot(DriverClass.clusterroot);
				frame.setVisible(true);
				frame.setContentPane(content);
				content.setBackground(Color.red);
				content.setLayout(new BorderLayout());
				content.add(dp, BorderLayout.CENTER);
				dp.setBackground(Color.WHITE);
			}
		});
	}

	public void handleConnectToDBButton() throws IOException {
		sequences = new ArrayList<String> ();
		extractFromDB(sequences);
		sequencesTextArea.setText("");
		sequencesTextArea.setWrapText(true);
		for (String sequence: sequences) {
			sequencesTextArea.setText(sequencesTextArea.getText() + sequence + " ");
		}
	}

	public void handleExtractXMLButton() throws Exception {
		sequences = new ArrayList<String>();
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose sequence:");
		fc.setInitialDirectory(new File("."));
		File file = fc.showOpenDialog(extractXMLButton.getScene().getWindow());
		extractFromXML(sequences, file);
		sequencesTextArea.setText("");
		sequencesTextArea.setWrapText(true);
		for (String sequence: sequences) {
			sequencesTextArea.setText(sequencesTextArea.getText() + sequence + " ");
		}
	}

	
	public static void extractFromDB (ArrayList<String> sequences) {
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/dna_sequences","dev_hagop","root");  
			Statement stmt = con.createStatement();  
			ResultSet rs = stmt.executeQuery("select * from dna");  
			while(rs.next())  
				sequences.add(rs.getString(4));  
			con.close();  
		} catch(Exception e){ 
			System.out.println(e);
		}
	}
	
	public static void extractFromXML (ArrayList<String> sequences, File file) throws Exception {
		try {
			SAXReader reader1 = new SAXReader();
			Document document1 = reader1.read(file);
			ArrayList<Node> sequences_XML = (ArrayList<Node>) document1.selectNodes("/DNADataBank/DNA");
			for (Node sequence_Node: sequences_XML) {
				sequences.add(sequence_Node.selectSingleNode("sequence").getText());
				System.out.println(sequences);
			}
		} catch (Exception e) {
			throw new Exception ("Extracting from XML went wrong");
		}
	}
	
	public static void saveToDB (ArrayList<Cluster> clusters) {
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/dna_sequences","dev_hagop","root");  
			Statement stmt = con.createStatement();  
			stmt.executeUpdate("delete from clusters");
			for(Cluster cluster: clusters) {
				String clusterName = cluster.getName();
				String clusterMedoid = cluster.getLeafNames().get(cluster.getRepresentativeIndex());
				String clusterSequences = "";
				for (String sequence: cluster.getLeafNames()) {
					clusterSequences += sequence + " ";
				}
				stmt.executeUpdate("INSERT INTO clusters VALUES (\'" + clusterName + "\',\'" + clusterMedoid + "\', \'" + clusterSequences + "\')");
			}
			con.close();  
		} catch(Exception e){ 
			System.out.println(e);
		}
	}
}