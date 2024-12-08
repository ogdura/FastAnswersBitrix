/**
 * Класс Deal используется для хранения данных о текущей сделки
 */
public class Deal {
    /**
     * Уникальный индетификатор сделки
     */
    private int deal_id;
    /**
     * Уникальный индетификатор чата
     */
    private int chat_id;
    /**
     * Уникальный индетификатор клиента
     */
    private int client_id;
    /**
     * Время последнего сообщения
     */
    private String date_message_last;
    /**
     * Уникальный индетификатор автора последнего сообщения
     */
    private int author_last_message_id;
    /**
     * Текущая стадия сделки
     */
    private String stage;
    /**
     * Предыдущая стадия сделки
     */
    private String last_stage;
    /**
     * Последнее сообщение
     */
    private String last_message;

    /**
     * Индетифицирует уникальный индетификатор сделки
     * @param deal_id уникальный индетификатор сделки
     */
    public void setDeal_id(int deal_id){
        if (deal_id>=0){
            this.deal_id = deal_id;
        }else{
            System.out.println("Ошибка! Deal_id должен быть > 0");
        }
    }
    /**
     * Индетифицирует уникальный индетификатор чата
     * @param chat_id уникальный индетификатор чата
     */
    public void setChat_id(int chat_id){
        if (chat_id>=0){
            this.chat_id = chat_id;
        }else{
            System.out.println("Ошибка! chat_id должен быть > 0");
        }
    }
    /**
     * Индетифицирует уникальный индетификатор клиента
     * @param client_id уникальный индетификатор клиента
     */
    public void setClient_id(int client_id){
        if (client_id>=0){
            this.client_id = client_id;
        }else{
            System.out.println("Ошибка! client_id должен быть > 0");
        }
    }
    /**
     * Индетифицирует уникальный индетификатор автора последнего сообщения
     * @param author_last_message_id уникальный индетификатор автора последнего сообщения
     */
    public void setAuthor_last_message_id(int author_last_message_id){
        if (author_last_message_id>=0){
            this.author_last_message_id = author_last_message_id;
        }else{
            System.out.println("Ошибка! author_last_message_id должен быть > 0");
        }
    }

    /**
     * Индетифицирует дату последнего сообщения
     * @param date_message_last
     */
    public void setDate_message_last(String date_message_last){
        this.date_message_last = date_message_last;
    }

    /**
     * @return Уникальный индетификатор сделки
     */
    public int getDeal_id(){
        return  deal_id;
    }
    /**
     * @return Уникальный индетификатор чата
     */
    public int getChat_id(){
        return chat_id;
    }
    /**
     * @return Уникальный индетификатор клиента
     */
    public int getClient_id(){
        return client_id;
    }
    /**
     * @return Уникальный индетификатор даты последнего сообщения
     */
    public String getDate_message_last(){
        return date_message_last;
    }
    /**
     * @return Уникальный индетификатор сделки
     */
    public int getAuthor_last_message_id(){
        return author_last_message_id;
    }
    /**
     * Индетифицирует текущею стадию сделки
     * @param stage Текущая стадия сделки
     */
    public void setStage(String stage) {
        this.stage = stage;
    }
    /**
     * @return Текущую стадию сделки
     */
    public String getStage() {
        return stage;
    }

    /**
     *Индетифицирует предыдущию стадию сделки
     * @param last_stage предыдущая стадия сделки
     */
    public void setLast_stage(String last_stage) {
        this.last_stage = last_stage;
    }
    /**
     * @return Уникальный индетификатор сделки
     */
    public String getLast_stage() {
        return last_stage;
    }

    /**
     *Индетифицирует последнее сообщение
     * @param last_message последнее сообщение
     */
    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }
    /**
     * @return Последнее сообщение
     */
    public String getLast_message() {
        return last_message;
    }
}
