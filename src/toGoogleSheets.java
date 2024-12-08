import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class toGoogleSheets {
    private static final String APPLICATION_NAME = "Google Sheets API";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final Sheets sheetsService;

    public toGoogleSheets(String credentialsFilePath) throws GeneralSecurityException, IOException {
        this.sheetsService = getSheetsService(credentialsFilePath);
    }

    private Sheets getSheetsService(String credentialsFilePath) throws GeneralSecurityException, IOException {
        // Используем FileInputStream для загрузки учетных данных
        FileInputStream credentialStream = new FileInputStream(credentialsFilePath);
        GoogleCredential credential = GoogleCredential.fromStream(credentialStream)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));
        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                (HttpRequestInitializer) credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void updateCell(String spreadsheetId, String range, String value) throws IOException {
        ValueRange body = new ValueRange().setValues(Collections.singletonList(Collections.singletonList(value)));
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }
}
