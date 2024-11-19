package ma.ensa.soapcompte.ws;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ma.ensa.soapcompte.bean.Compte;
import ma.ensa.soapcompte.bean.TypeCompte;

public class SoapService {

    private static final String NAMESPACE = "http://ws.soap.demo.TPSOAP.ensa.com/";
    private static final String URL = "http://10.0.2.2:8080/services/ws?wsdl"; // Changez ceci en fonction de votre adresse IP
    private static final String METHOD_GET_COMPTES = "getComptes";
    private static final String METHOD_CREATE_COMPTE = "createCompte";
    private static final String METHOD_DELETE_COMPTE = "deleteCompte";
    private static final String SOAP_ACTION = "";

    // Get list of comptes from the web service
    public List<Compte> getComptes() throws Exception {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_COMPTES);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE transport = new HttpTransportSE(URL);
        transport.debug = true;
        transport.call(SOAP_ACTION, envelope);

        SoapObject response = (SoapObject) envelope.bodyIn;
        List<Compte> comptes = new ArrayList<>();

        for (int i = 0; i < response.getPropertyCount(); i++) {
            SoapObject soapCompte = (SoapObject) response.getProperty(i);
            Compte compte = new Compte(
                    Long.parseLong(soapCompte.getPropertySafely("id", "0").toString()),
                    Double.parseDouble(soapCompte.getPropertySafely("solde", "0").toString()),
                    new Date(), // Assuming date parsing isn't needed yet
                    TypeCompte.valueOf(soapCompte.getPropertySafely("type", "EPARGNE").toString())
            );
            comptes.add(compte);
        }

        return comptes;
    }

    // Create a new compte
    public boolean createCompte(double solde, TypeCompte type) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_CREATE_COMPTE);
        request.addProperty("solde", String.valueOf(solde));
        request.addProperty("type", type.name());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        try {
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.debug = true;
            transport.call(SOAP_ACTION, envelope);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a compte by ID
    public boolean deleteCompte(long id) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_DELETE_COMPTE);
        request.addProperty("id", String.valueOf(id));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        try {
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.debug = true;
            transport.call(SOAP_ACTION, envelope);
            return (Boolean) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
