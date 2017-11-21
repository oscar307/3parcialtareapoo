package sample.Controladores;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import sample.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {
    public TextField txtPais;
    public ListView<String> lvPaises;
    public ListView<String> lvCiudades;
    public Label lbTitulo;
    public Label lbError;
    public Label lblID;
    public Label lblCodigo;
    public Label lblDistrito;
    public Label lblPoblacion;
    private ObservableList listaPaises = FXCollections.observableArrayList();
    private ObservableList listaCiudades = FXCollections.observableArrayList();

    private String paisSeleccionado;
    private String ciudadSeleccionada;

    @FXML
    public void initialize(){
        lvPaises.setItems(listaPaises);
        lvCiudades.setItems(listaCiudades);
        lvPaises.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                paisSeleccionado = newValue;
                listaCiudades.clear();
                try{
                    Connection con = Main.getConexion();
                    Statement stmt = con.createStatement();
                    String sql = "SELECT Name FROM city WHERE countrycode=(SELECT Code FROM country WHERE Name='"+paisSeleccionado+"')";
                    ResultSet resultado = stmt.executeQuery(sql);
                    while (resultado.next()){
                        listaCiudades.add(resultado.getString("Name"));
                    }
                }catch (SQLException e){
                    System.out.println(e.getMessage());
                }
            }
        });

        lvCiudades.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ciudadSeleccionada = newValue;
                limpiar();
                try {
                    Connection con = Main.getConexion();
                    Statement stmt = con.createStatement();
                    String sql="SELECT * FROM city WHERE name='" + ciudadSeleccionada +"'";
                    ResultSet resultado = stmt.executeQuery(sql);
                    while (resultado.next()){
                        lblID.setText(resultado.getString("ID"));
                        lblCodigo.setText(resultado.getString("CountryCode"));
                        lblDistrito.setText(resultado.getString("District"));
                        lblPoblacion.setText(resultado.getString("Population"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void buscarPais(KeyEvent keyEvent){
        listaPaises.clear();
       String nombreBusqueda = txtPais.getText().trim();
        if (nombreBusqueda.length() >= 1){
            Connection con = Main.getConexion();
            try {
                Statement stnt = con.createStatement();
                String sql = "SELECT Name FROM country WHERE Name LIKE '"+ nombreBusqueda+ "%'";
                ResultSet resultado = stnt.executeQuery(sql);
                while (resultado.next()){
                    listaPaises.add(resultado.getString("Name"));
                }
                resultado.close();
            } catch (SQLException e) {
                lbError.setText(e.getMessage());
            }
        }
    }
    public void limpiar(){
        lblPoblacion.setText("");
        lblID.setText("");
        lblCodigo.setText("");
        lblDistrito.setText("");
    }

}



