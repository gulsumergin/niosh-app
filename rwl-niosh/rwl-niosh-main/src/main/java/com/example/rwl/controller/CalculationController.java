package com.example.rwl.controller;

import com.example.rwl.model.CalculationRequest;
import com.example.rwl.model.CouplingType;
import com.example.rwl.service.CalculationService;
import com.example.rwl.service.Risk;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;


@Controller
@RequestMapping("/")
public class CalculationController {

    private final CalculationService calculationService;
    private final TemplateEngine templateEngine;

    @Autowired
    public CalculationController(CalculationService calculationService, TemplateEngine templateEngine) {
        this.calculationService = calculationService;
        this.templateEngine = templateEngine;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/learn")
    public String learn() {
        return "learn";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/calculate")
    public String showCalculationForm() {
        return "calculationForm";
    }

    @PostMapping("/calculate")
    public String calculate(@ModelAttribute CalculationRequest calculationRequest, BindingResult bindingResult, Model model)  {

        if (bindingResult.hasErrors()) {
            return "redirect:/calculate";
        }

        double loadConstant = calculationRequest.getLoadConstant();
        double H = calculationRequest.getH();
        double V = calculationRequest.getV();
        double D = calculationRequest.getD();
        double A = calculationRequest.getA();
        double F = calculationRequest.getF();
        double workDuration = calculationRequest.getWorkDuration();
        CouplingType couplingType = calculationRequest.getCouplingType();
        double L = calculationRequest.getL();


        double HM = calculationService.horizontalMultiplier(H);
        double VM = calculationService.verticalMultiplier(V);
        double DM = calculationService.distanceMultiplier(D);
        double AM = calculationService.asymmetryMultiplier(A);
        double FM = calculationService.frequencyMultiplier(F, V, workDuration);
        double CM = calculationService.couplingMultiplier(V, couplingType);
        double RWL = calculationService.calculateRWL(loadConstant, HM, VM, DM, AM, FM, CM);
        double LI = calculationService.loadIndex(L, RWL);

        // Check if the condition is met
        if (LI== 0) {
            // Condition met, disable form
            return "redirect:/fail?li=" + LI + "&H=" + H  + "&V=" + V + "&D=" + D + "&A=" + A + "&F=" + F
                    + "&workDuration=" + workDuration + "&couplingType=" + couplingType + "&L=" + L;
        }


        return "redirect:/result?hm=" + HM + " + &vm=" + VM + " &dm=" + DM + "&am=" + AM + "&fm=" + FM + "&cm=" + CM +
                "&rwl=" + RWL + "&li=" + LI + "&H=" + H  + "&V=" + V + "&D=" + D + "&A=" + A + "&F=" + F
                + "&workDuration=" + workDuration + "&couplingType=" + couplingType + "&L=" + L;
    }

    @GetMapping("/fail")
    public String fail(@RequestParam("H") double H,
                       @RequestParam("V") double V,
                       @RequestParam("D") double D,
                       @RequestParam("A") double A,
                       @RequestParam("F") double F,
                       @RequestParam("workDuration") double workDuration,
                       @RequestParam("couplingType") CouplingType couplingType,
                       @RequestParam("L") double L,
                       Model model){

        model.addAttribute("H", H);
        model.addAttribute("V", V);
        model.addAttribute("D", D);
        model.addAttribute("A", A);
        model.addAttribute("F", F);
        model.addAttribute("workDuration", workDuration);
        model.addAttribute("couplingType", couplingType);
        model.addAttribute("L", L);

        return "fail";
    }


    @GetMapping("/result")
    public String showResult(@RequestParam("hm") double hm,
                             @RequestParam("vm") double vm,
                             @RequestParam("dm") double dm,
                             @RequestParam("am") double am,
                             @RequestParam("fm") double fm,
                             @RequestParam("cm") double cm,
                             @RequestParam("rwl") double rwl,
                             @RequestParam("li") double li,
                             @RequestParam("H") double H,
                             @RequestParam("V") double V,
                             @RequestParam("D") double D,
                             @RequestParam("A") double A,
                             @RequestParam("F") double F,
                             @RequestParam("workDuration") double workDuration,
                             @RequestParam("couplingType") CouplingType couplingType,
                             @RequestParam("L") double L,
                             Model model) {

        // Add the calculated attributes to the model to be displayed in the view
        model.addAttribute("hm", hm);
        model.addAttribute("vm", vm);
        model.addAttribute("dm", dm);
        model.addAttribute("am", am);
        model.addAttribute("fm", fm);
        model.addAttribute("cm", cm);
        model.addAttribute("rwl", rwl);
        model.addAttribute("li", li);
        model.addAttribute("H", H);
        model.addAttribute("V", V);
        model.addAttribute("D", D);
        model.addAttribute("A", A);
        model.addAttribute("F", F);
        model.addAttribute("workDuration", workDuration);
        model.addAttribute("couplingType", couplingType);
        model.addAttribute("L", L);


        String recommendAction = riskValue(li)[0];
        String risk = riskValue(li)[1];

        model.addAttribute("risk", risk);
        model.addAttribute("recommendAction", recommendAction);

        // Return the name of the view page to be rendered
        return "result";
    }

    @GetMapping("/view")
    public String viewPage(Model model) {
        // Add attributes to the model as needed
        return "view";
    }

    @RequestMapping("/download")
    public ResponseEntity<InputStreamResource> downloadPDF(@RequestParam("hm") double hm,
                                                           @RequestParam("vm") double vm,
                                                           @RequestParam("dm") double dm,
                                                           @RequestParam("am") double am,
                                                           @RequestParam("fm") double fm,
                                                           @RequestParam("cm") double cm,
                                                           @RequestParam("rwl") double rwl,
                                                           @RequestParam("li") double li,
                                                           @RequestParam("H") double H,
                                                           @RequestParam("V") double V,
                                                           @RequestParam("D") double D,
                                                           @RequestParam("A") double A,
                                                           @RequestParam("F") double F,
                                                           @RequestParam("L") double L,
                                                           @RequestParam("workDuration") double workDuration,
                                                           @RequestParam("couplingType") CouplingType couplingType,
                                                           @RequestParam("risk") String risk,
                                                           @RequestParam("recommendedAction") String recommendedAction) throws DocumentException, IOException {

        String color = "#4169E1";
        /* Generate HTML content using Thymeleaf */
        Context context = new Context();
        context.setVariable("hm", hm);
        context.setVariable("vm", vm);
        context.setVariable("dm", dm);
        context.setVariable("am", am);
        context.setVariable("fm", fm);
        context.setVariable("cm", cm);
        context.setVariable("l", L);
        context.setVariable("rwl", rwl);
        context.setVariable("li", li);
        context.setVariable("H", H);
        context.setVariable("V", V);
        context.setVariable("D", D);
        context.setVariable("A", A);
        context.setVariable("F", F);
        context.setVariable("workDuration", workDuration);
        context.setVariable("couplingType", couplingType);
        context.setVariable("risk", risk);
        context.setVariable("recommendedAction", recommendedAction);
        context.setVariable("color", color);

        context.setVariable("message", "This is a test PDF content.");
        String html = templateEngine.process("view", context);

        /* Convert HTML to PDF */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(html));
        document.close();

        /* Return the PDF as a downloadable file */
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=download.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }
    
    private String[] riskValue(double LI) {
        String recommendAction = "";
        String risk = "";
        if(LI == 0){
            recommendAction = "Inputs are invalid.";
            risk = Risk.INVALID.getRisk();
        }
        else if(LI <= 1){
            recommendAction = "None in general for the healthy working population.";
            risk = Risk.VERY_LOW.getRisk();
        }
        else if(1 < LI && LI <= 1.5){
            recommendAction = "In particular pay attention to low frequency/high load conditions and to extreme or" +
                    " static postures. Include all factors in redesigning tasks or workstations and consider efforts to" +
                    " lower the LI to values ≤ 1,0.";
            risk = Risk.LOW.getRisk();
        }
        else if(1.5 < LI && LI <=2){
            recommendAction = "Redesign tasks and workplaces according to priorities to reduce the LI, followed by" +
                    " analysis of results to confirm effectiveness.";
            risk = Risk.MODERATE.getRisk();
        }
        else if(2 < LI && LI <=3){
            recommendAction = "Changes to task to reduce the LI should be a high priority.";
            risk = Risk.HIGH.getRisk();
        }
        else if(LI > 3){
            recommendAction = "Changes to task to reduce the LI should be made immediately.";
            risk = Risk.VERY_HIGH.getRisk();
        }
        else {
            recommendAction = "Inputs are invalid.";
            risk = Risk.INVALID.getRisk();
        }
        String[] response = {recommendAction, risk};
        return response;
    }
}
