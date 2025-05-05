package com.example.person_management.controller;

import com.example.person_management.dto.PersonResponseDTO;
import com.example.person_management.service.PersonService;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final PersonService personService;
    private final TemplateEngine templateEngine;

    @GetMapping(value = "/csv", produces = "text/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=persons.csv");

        List<PersonResponseDTO> persons = personService.getAllPersons();
        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            // Write header
            writer.writeNext(new String[]{"ID", "Name", "Surname", "Email", "Phone", "Birthdate", "Address"});
            // Write records
            for (PersonResponseDTO p : persons) {
                writer.writeNext(new String[]{
                        p.id().toString(), p.name(), p.surname(), p.email(), p.phone(),
                        p.birthdate().toString(), p.address()
                });
            }
        }
    }

    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PersonResponseDTO> exportJson() {
        return personService.getAllPersons();
    }

    @GetMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void exportPdf(HttpServletResponse response) throws Exception {
        List<PersonResponseDTO> persons = personService.getAllPersons();

        Context context = new Context();
        context.setVariable("persons", persons);

        String html = templateEngine.process("person-report", context); // uses templates/person-report.html

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();

        response.setHeader("Content-Disposition", "attachment; filename=persons.pdf");
        renderer.createPDF(response.getOutputStream());
    }
}

