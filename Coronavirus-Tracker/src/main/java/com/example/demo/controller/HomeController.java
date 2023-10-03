package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.model.LocationStates;
import com.example.demo.services.CoronaVirusDataServices;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
	
	@Autowired
    private  CoronaVirusDataServices crnService;


    @GetMapping("/")
    public String home(Model model) {
        try {
            List<LocationStates> allstates = crnService.getAllStats();
            int totalDeathsReported = allstates.stream().mapToInt(stat -> stat.getLatestTotalDeaths()).sum();
            model.addAttribute("LocationStates", allstates);
            model.addAttribute("totalDeathsReported", totalDeathsReported);
        } catch (Exception e) {
            // Handle exceptions gracefully, e.g., log the error
            e.printStackTrace();
        }
        return "home";
    }
    @GetMapping("/viewchart")
    public String viewChart(Model model) {
        try {
            // Fetch the data needed for the chart
            List<LocationStates> allStates = crnService.getAllStats();

            // Extract the data you want to use for the chart (e.g., total deaths)
            List<String> labels = new ArrayList<>(); 
            List<Integer> totalDeathsData = new ArrayList<>(); 
            for (LocationStates state : allStates) {
                labels.add(state.getState());
                totalDeathsData.add(state.getLatestTotalDeaths()); 
            }

            // Add the chart data to the model
            model.addAttribute("labels", labels);
            model.addAttribute("totalDeathsData", totalDeathsData);

        } catch (Exception e) {
            // Handle exceptions gracefully
            e.printStackTrace();
        }

        return "viewchart"; // 
    }

    
}
