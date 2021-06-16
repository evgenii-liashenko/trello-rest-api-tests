package services;

import beans.TrelloBoard;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

import static core.api.TrelloBoardsApi.makeBoardObject;
import static core.api.TrelloBoardsApi.trelloBoardRequestBuilder;
import static core.api.TrelloMembersApi.makeBoardObjects;
import static core.api.TrelloMembersApi.trelloMembersRequestBuilder;

public class BoardService {

    public String createBoardOnTrello(TrelloBoard boardData) {
        Response response = trelloBoardRequestBuilder()
                .setMethod(Method.POST)
                .setName(boardData.getName())
                .setDescription(boardData.getDesc())
                .buildRequest()
                .sendRequest();
        String generatedId = makeBoardObject(response).getId();
        return generatedId;
    }

    public TrelloBoard getBoardFromTrello(String boardId) {        //String boardId
        Response response = trelloBoardRequestBuilder()
                .setId(boardId)
                .buildRequest()
                .sendRequest();
        return makeBoardObject(response);
    }

    public void deleteBoardFromTrello(String boardId) {
        trelloBoardRequestBuilder()
                .setMethod(Method.DELETE)
                .setId(boardId)
                .buildRequest()
                .sendRequest();
    }

    public List<String> getAllBoardIdsInTrello() {
        Response response = trelloMembersRequestBuilder()
                .setFirstPathSuffix("me")
                .setSecondPathSuffix("boards")
                .setField("id")
                .buildRequest()
                .sendRequest();

        List<TrelloBoard> boardObjects = makeBoardObjects(response);
        List<String> idsOnly = new ArrayList<>();
        for (TrelloBoard item : boardObjects) {
            idsOnly.add(item.getId());
        }

        return idsOnly;
    }
}
