package services;

import beans.TrelloList;
import io.restassured.http.Method;
import io.restassured.response.Response;

import static core.api.TrelloListApi.makeListObject;
import static core.api.TrelloListApi.trelloListRequestBuilder;

public class ListService {

    public String createListOnTrello(String name, String boardId) {
        Response response = trelloListRequestBuilder()
                .setMethod(Method.POST)
                .setName(name)
                .setBoardId(boardId)
                .buildRequest()
                .sendRequest();
        String generatedId = makeListObject(response).getId();
        return generatedId;
    }

    public TrelloList getListFromTrello(String listId) {
        Response response = trelloListRequestBuilder()
                .setListId(listId)
                .buildRequest()
                .sendRequest();
        return makeListObject(response);
    }
}
