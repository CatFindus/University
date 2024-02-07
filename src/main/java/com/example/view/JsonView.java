package com.example.view;

import com.example.mapper.viewmapper.JsonMapper;
import com.example.model.dto.Response.DtoResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
public class JsonView implements View {
    private HttpServletResponse response;
    private JsonMapper mapper;

    public JsonView(HttpServletResponse response) {
        this.response = response;
        mapper = new JsonMapper();
    }

    @Override
    public void update(List<DtoResponse> units) {
        String responseBody = mapper.getStringFromResponse(units);
        response.setContentType("application/json");
        try(PrintWriter writer = response.getWriter()) {
            writer.print(responseBody);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
