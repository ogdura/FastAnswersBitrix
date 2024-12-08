import java.io.*;
import java.sql.SQLOutput;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Основной класс программы
 */
public class Main {
    /**
     * Основной метод программы, которая отправляет сделки в этап быстрые ответы, если клиенту не отвечено более 2 минут.
     * @param args
     * @throws IOException
     */




    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {

            }
        }));

        String envPath = "src\\config.env";
        EnvReader env = new EnvReader(envPath);
        String Token = env.getValue("TOKEN");
        try {
            String webhookUrl = Token+"crm.deal.list.json";
            while(true) {
                long sumDeal = 0;
                int countAnswer = 0;
                int start = 0;
                int batchSize = 50;
                boolean hasMore = true;
                String first_str = null;
                //["ID", "TITLE", "LEAD_ID", "STAGE_ID", "CONTACT_ID", "OPPORTUNITY", "CURRENCY_ID"]
                while (hasMore) {
                    // Фильтр: категория тп фл(19), исключение стадий "новые", "проиграна", "завершена"
                    String jsonInputString = String.format(
                            "{\"filter\": {\"CATEGORY_ID\":\"19\", \"!STAGE_ID\": [\"C19:NEW\", \"C19:LOSE\", \"C19:WON\"]}," +
                                    "\"select\":[\"ID\", \"TITLE\", \"LEAD_ID\", \"STAGE_ID\", \"CONTACT_ID\", \"OPPORTUNITY\", \"CURRENCY_ID\", \"UF_CRM_1729797083100\"]," + // <- здесь добавлена запятая
                                    "\"start\": %d}", start
                    );



                    WebHook hook = new WebHook();
                    hook.setWebhookUrl(webhookUrl);
                    String response = hook.getJson(jsonInputString);//Получили пакет сделок

                    if(response.indexOf("next")==-1){
                        hasMore = false;
                    }

                        //JSONObject jsonObj = new JSONObject(response.toString());
                        //System.out.println(response.toString());
                        System.out.println("package" + start);
                        //System.out.println("len res" + response.length());
                        //System.out.println(response);
                        String did = response;
                    //did = did.substring(did.indexOf(':') + 1, did.indexOf(']') + 1);
                        while (did.indexOf("\"ID\":\"") != -1) {
                            System.out.println();
                            System.out.println("aaa" + did);


                            Deal deal = new Deal();
                            deal.setDeal_id(Integer.parseInt(did.substring(did.indexOf("\"ID\":\"") + 6, did.indexOf(",") - 1)));
                            String StageDeal = did.substring(did.indexOf("\"STAGE_ID\":")+12);
                            StageDeal = StageDeal.substring(0,StageDeal.indexOf(',')-1);
                            deal.setStage(StageDeal);

                            String LastStage = did.substring(did.indexOf("\"UF_CRM_1729797083100\":"));
                            LastStage = LastStage.substring(LastStage.indexOf(':')+1,LastStage.indexOf(',')-1);
                            deal.setLast_stage(LastStage);
                            System.out.println("last_stage"+LastStage);


                            String webhookchaturl = Token+"imopenlines.crm.chat.get.json?CRM_ENTITY_TYPE=DEAL";






                            String jsonInputStringForChat = String.format(
                                    "{\"CRM_ENTITY\": %d}",
                                    deal.getDeal_id()
                            );
                            WebHook chat = new WebHook();
                            chat.setWebhookUrl(webhookchaturl);
                            String responseChat = chat.getJson(jsonInputStringForChat);
                            //System.out.println(responseChat);
                            String chatStr = responseChat.substring(responseChat.toString().indexOf("["));

                            while (chatStr.indexOf("CHAT_ID") != -1)  {
                                System.out.println("sumdeal nach"+sumDeal);
                                System.out.println("count nach"+countAnswer);
                                System.out.println("chat_id="+chatStr.substring(chatStr.indexOf(':') + 2, chatStr.indexOf(',') - 1));
                                deal.setChat_id(Integer.parseInt(chatStr.substring(chatStr.indexOf(':') + 2, chatStr.indexOf(',') - 1)));
                                chatStr = chatStr.substring(chatStr.indexOf("}") + 2);

                                //получаем инфу про

                               String clUrl = Token+"imopenlines.dialog.get.json";
                               String jsonClient = String.format(
                                       "{\"CHAT_ID\": %d}",
                                       deal.getChat_id()
                               );
                               WebHook client = new WebHook();
                               client.setWebhookUrl(clUrl);
                               String clientData = client.getJson(jsonClient);
                               clientData = clientData.substring(clientData.indexOf("user_id")+8);
                                System.out.println("clientid="+clientData.substring(clientData.indexOf(":")+1,clientData.indexOf(",")));
                                clientData = clientData.substring(clientData.indexOf(":")+1,clientData.indexOf(","));
                                deal.setClient_id(Integer.parseInt(clientData));


                                String messageUrl = Token+"im.dialog.messages.get.json";
                                //System.out.println("chattttt"+deal.getChat_id() );
                                String jsonMessage = String.format(
                                            "{\"DIALOG_ID\": \"chat%s\"}",
                                            deal.getChat_id()
                                    );
                               // System.out.println(jsonMessage);
                                WebHook messageHook = new WebHook();
                                messageHook.setWebhookUrl(messageUrl);
                                String messageData = messageHook.getJson(jsonMessage);
                                //System.out.println(messageData);
                                String last_message_id = messageData.substring(messageData.indexOf("author_id"));
                                String lastMessage = messageData;
                                lastMessage = last_message_id.substring(last_message_id.indexOf("text")+5);
                                lastMessage = lastMessage.substring(0,lastMessage.indexOf("unread")-2);
                                //String param_last = last_message_id.substring(0,last_message_id.indexOf("\"id"));
                                //param_last = last_message_id.substring(0, last_message_id.indexOf("\"id"));
                                String param_last = last_message_id.substring(last_message_id.indexOf("params") + 10);
                                System.out.println("----"+param_last);
                                param_last = param_last.substring(0,param_last.indexOf('}'));
                                System.out.println("param_last_vivod "+param_last);
                                last_message_id = last_message_id.substring(last_message_id.indexOf(":")+1,last_message_id.indexOf(','));

                                WordsRead words = new WordsRead();
                                List <String> str = new ArrayList<>();
                                str = words.getWords();
                                boolean blockWord = false;
                                for(int i = 0; i < str.size();i++){
                                    if(lastMessage.toLowerCase().indexOf(str.get(i).toLowerCase())!=-1){
                                        blockWord = true;
                                        System.out.println("СХОДСТВО С "+str.get(i));
                                        break;
                                    }
                                }
                                while((Integer.parseInt(last_message_id)==0|param_last.indexOf("bx-messenger-content-item-system")!=-1|blockWord) & messageData.indexOf("author_id") !=-1) {

                                    last_message_id = messageData.substring(messageData.indexOf("author_id"));


                                    //param_last = last_message_id.substring(0, last_message_id.indexOf("\"id"));

                                    lastMessage = last_message_id.substring(last_message_id.indexOf("text") + 5);
                                    lastMessage = lastMessage.substring(0, lastMessage.indexOf("unread") - 2);

                                    param_last = last_message_id.substring(last_message_id.indexOf("params") + 10);
                                    param_last = param_last.substring(0, param_last.indexOf('}'));
                                    last_message_id = last_message_id.substring(last_message_id.indexOf(":") + 1, last_message_id.indexOf(','));
                                    for (int i = 0; i < str.size(); i++) {
                                        if (lastMessage.toLowerCase().indexOf(str.get(i).toLowerCase()) != -1 & Integer.parseInt(last_message_id) == deal.getClient_id()) {
                                            blockWord = true;
                                            System.out.println("СХОДСТВО С " + str.get(i));
                                            break;
                                        } else {
                                            blockWord = false;
                                        }
                                    }
                                    if (messageData.indexOf("author_id") != -1) {
                                        messageData = messageData.substring(messageData.indexOf("author_id") + 10);
                                    }
                                    System.out.println("тест бота"+last_message_id+" "+messageData);

                                }
                                System.out.println("MESSAGE DATA========"+lastMessage);
                                System.out.println("ALL MESSAGES========"+messageData);
                                String last_time = messageData.substring(messageData.indexOf("date"));
                                last_time = last_time.substring(last_time.indexOf(":")+2,last_time.indexOf(',')-1);
                                deal.setAuthor_last_message_id(Integer.parseInt(last_message_id));
                                deal.setDate_message_last(last_time);
                                ZonedDateTime zonedDateTime = ZonedDateTime.parse(deal.getDate_message_last());
                                Duration difTime = Duration.between(zonedDateTime,ZonedDateTime.now());
                                System.out.println("minutes"+difTime.toMinutes());
                                System.out.println("ТЕКУЩАЯ СТАДИЯ СДЕЛКИ===="+deal.getStage());


                                String UpdateDealURL = Token+"crm.deal.update.json";
                                String jsonUpdateDeal = String.format(
                                        "{\"id\": %d,\"fields\": {\"STAGE_ID\":\"C19:1\",\"UF_CRM_1729797083100\": \"%s\"}}", deal.getDeal_id(), deal.getStage()
                                );


                                if(!deal.getStage().equals("C19:1") & !deal.getStage().equals("C19:UC_XCXVM2")&!deal.getStage().equals("C19:UC_35RUVV")){
                                    if((deal.getClient_id()==deal.getAuthor_last_message_id()/*|deal.getAuthor_last_message_id()==0*/)) {
                                        if(difTime.toMinutes()>2){
                                        System.out.println("АПДЕЙТ");
                                        System.out.println(">2" + jsonUpdateDeal);
                                        WebHook updatedealHook = new WebHook();
                                        updatedealHook.setWebhookUrl(UpdateDealURL);
                                        String updateDealData = updatedealHook.getJson(jsonUpdateDeal);


                                        System.out.println(">2" + updateDealData);
                                        }else{
                                            if(messageData.indexOf("author_id")!=-1) {
                                                messageData = messageData.substring(messageData.indexOf("author_id") + 10);
                                            }
                                            boolean update = false;
                                            while(messageData.indexOf("author_id")!=-1){
                                                last_message_id = messageData.substring(messageData.indexOf("author_id"));
                                                last_message_id = last_message_id.substring(last_message_id.indexOf(":")+1,last_message_id.indexOf(','));
                                                last_time = messageData.substring(messageData.indexOf("date"));
                                                last_time = last_time.substring(last_time.indexOf(":")+2,last_time.indexOf(',')-1);
                                                zonedDateTime = ZonedDateTime.parse(last_time);
                                                difTime = Duration.between(zonedDateTime,ZonedDateTime.now());
                                                if(Integer.parseInt(last_message_id)==deal.getClient_id()&difTime.toMinutes()>2){
                                                    update = true;
                                                    break;
                                                }
                                                if(messageData.indexOf("author_id")!=-1) {
                                                    messageData = messageData.substring(messageData.indexOf("author_id") + 10);
                                                }
                                            }

                                            if(update){
                                                System.out.println("АПДЕЙТ СПАМА");
                                                System.out.println(">2" + jsonUpdateDeal);
                                                WebHook updatedealHook = new WebHook();
                                                updatedealHook.setWebhookUrl(UpdateDealURL);
                                                String updateDealData = updatedealHook.getJson(jsonUpdateDeal);
                                            }
                                        }
                                    }else{
                                        if(!deal.getStage().equals("C19:UC_XCXVM2")&!deal.getStage().equals("C19:UC_35RUVV")){

                                            ZonedDateTime not_client_time = ZonedDateTime.parse(deal.getDate_message_last());
                                            System.out.println("not_client_time              "+not_client_time);
                                            while(messageData.indexOf("author_id")!=-1&deal.getClient_id()!=Integer.parseInt(last_message_id)){

                                                last_message_id = messageData.substring(messageData.indexOf("author_id"));
                                                last_message_id = last_message_id.substring(last_message_id.indexOf(":")+1,last_message_id.indexOf(','));
                                                System.out.println("LAST MESSAGE ID========"+last_message_id);

                                                if(messageData.indexOf("author_id")!=-1) {
                                                    messageData = messageData.substring(messageData.indexOf("author_id") + 10);
                                                }
                                            }
                                            if(messageData.indexOf("date")!=-1){
                                                last_time = messageData.substring(messageData.indexOf("date"));
                                                last_time = last_time.substring(last_time.indexOf(":")+2,last_time.indexOf(',')-1);
                                                zonedDateTime = ZonedDateTime.parse(last_time);
                                                difTime = Duration.between(zonedDateTime,not_client_time);
                                            }

                                            sumDeal = sumDeal + difTime.toMinutes();
                                            countAnswer = countAnswer + 1;
                                            System.out.println("sumdeal obnov"+sumDeal);
                                            System.out.println("count obnov"+countAnswer);
                                        }
                                    }
                                }else if(deal.getClient_id()!=deal.getAuthor_last_message_id() & deal.getStage().equals("C19:1")){


                                    ZonedDateTime not_client_time = ZonedDateTime.parse(deal.getDate_message_last());
                                    System.out.println("not_client_time              "+not_client_time);
                                        while(messageData.indexOf("author_id")!=-1&deal.getClient_id()!=Integer.parseInt(last_message_id)){

                                            last_message_id = messageData.substring(messageData.indexOf("author_id"));
                                            last_message_id = last_message_id.substring(last_message_id.indexOf(":")+1,last_message_id.indexOf(','));
                                            System.out.println("LAST MESSAGE ID========"+last_message_id);

                                            if(messageData.indexOf("author_id")!=-1) {
                                                messageData = messageData.substring(messageData.indexOf("author_id") + 10);
                                            }
                                        }
                                        if(messageData.indexOf("date")!=-1){
                                            last_time = messageData.substring(messageData.indexOf("date"));
                                            last_time = last_time.substring(last_time.indexOf(":")+2,last_time.indexOf(',')-1);
                                            zonedDateTime = ZonedDateTime.parse(last_time);
                                            difTime = Duration.between(zonedDateTime,not_client_time);
                                        }


                                        sumDeal = sumDeal + difTime.toMinutes();
                                        countAnswer = countAnswer + 1;
                                    System.out.println("sumdeal быстрые ответы"+sumDeal);
                                    System.out.println("count быстрые ответы"+countAnswer);


                                    String GetAllStageURL = Token+"crm.dealcategory.stage.list.json?ID=19";
                                    WebHook AllStageHook = new WebHook();
                                    AllStageHook.setWebhookUrl(GetAllStageURL);
                                    String AllStage = AllStageHook.getJson(null);
                                    System.out.println("ALL STAGE =========="+AllStage);
                                    String STATUS_ID;
                                    boolean is_update_to_new = true;

                                    while(AllStage.indexOf("STATUS_ID")!=-1){
                                        STATUS_ID = AllStage.substring(AllStage.indexOf("STATUS_ID"));
                                        STATUS_ID = STATUS_ID.substring(STATUS_ID.indexOf(':')+2);
                                        STATUS_ID = STATUS_ID.substring(0,STATUS_ID.indexOf('"'));
                                        System.out.println("deal.getLast_stage==="+deal.getLast_stage());
                                        System.out.println("STATUS ID"+STATUS_ID);
                                        if(deal.getLast_stage().equals('"'+STATUS_ID+'"')){
                                            is_update_to_new = false;
                                            break;
                                        }

                                        AllStage = AllStage.substring(AllStage.indexOf('}')+1);
                                        System.out.println("STATUS_ID======"+STATUS_ID);
                                    }



                                    UpdateDealURL = Token+"crm.deal.update.json";
                                    if(!is_update_to_new) {
                                        jsonUpdateDeal = String.format(
                                                "{\"id\": %d,\"fields\": {\"STAGE_ID\":%s,\"UF_CRM_1729797083100\": null}}", deal.getDeal_id(), deal.getLast_stage()
                                        );
                                    }else{
                                         jsonUpdateDeal = String.format(
                                                "{\"id\": %d,\"fields\": {\"STAGE_ID\":\"C19:NEW\",\"UF_CRM_1729797083100\": null}}", deal.getDeal_id()
                                        );
                                    }
                                    WebHook updatedealHook = new WebHook();
                                    updatedealHook.setWebhookUrl(UpdateDealURL);
                                    String updateDealData = updatedealHook.getJson(jsonUpdateDeal);


                                    System.out.println("ОТПРАВКА ОБРАТНО"+updateDealData);
                                }
                        }


                            System.out.println(did.substring(did.indexOf("\"ID\":\"") + 6, did.indexOf(",") - 1));
                            did = did.substring(did.indexOf("}")+2);
                        }
                            if (start == 0) {
                                first_str = response.toString();
                            } else {
                                //System.out.println("first="+first_str);
                                //System.out.println("this="+response.toString().substring(response.toString().indexOf("\"ID\":\""),response.toString().indexOf(",")));
                                if (first_str.equals(response.toString().substring(response.toString().indexOf("\"ID\":\""), response.toString().indexOf(",")))) {
                                    System.out.println(true);
                                    hasMore = false;
                                }

                            }
                            if (response.length() < batchSize) {
                                hasMore = false;
                            } else {
                                start += batchSize;
                            }
                            if(!hasMore){
                                float resTime = sumDeal/countAnswer;
                                System.out.println("RES TIME ====="+resTime);
                                String credentialsPath = "Files\\fair-analogy-443818-g1-7c57ef651159.json";
                                String spreadsheetId = "1bSGACAsAyP3I9Ttbm0nh_IaezwX1DB7zPC7jSzhQttY";
                                String range = "Лист1!A2";
                                String value = String.valueOf(resTime);

                                try {
                                    toGoogleSheets googleSheetsService = new toGoogleSheets(credentialsPath);
                                    googleSheetsService.updateCell(spreadsheetId, range, value);
                                    System.out.println("Данные успешно добавлены в Google Sheets!");
                                } catch (Exception e) {
                                    System.err.println("Ошибка при обновлении таблицы: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
