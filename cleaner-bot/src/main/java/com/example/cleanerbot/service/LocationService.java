package com.example.cleanerbot.service;

import com.example.cleanerbot.domain.request.LocationReq;
import com.example.cleanerbot.domain.response.LocationRes;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RequiredArgsConstructor
public class LocationService {
	public static final String FILE_PATH= "classpath:data/location_info.xlsx";

	private final ResourceLoader resourceLoader;


	public LocationRes readLocationValueOnFile(LocationReq locationReq) throws IOException {
//		File file = new File(FILE_PATH);
		Resource resource = resourceLoader.getResource(FILE_PATH);
//		FileInputStream fileInputStream = new FileInputStream(FILE_PATH);
		Workbook workbook = WorkbookFactory.create(resource.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);

		for (Row row : sheet) {
			if (row==null) break;

			String conditionA = row.getCell(0).toString();
			String conditionB = row.getCell(1).toString();
			String conditionC = row.getCell(2).toString();
			if (conditionA.contains(locationReq.getArea1())
					&& conditionB.contains(locationReq.getArea2())
						&& conditionC.contains(locationReq.getArea3())){

				return LocationRes.builder()
						.x((row.getCell(3).toString()))
						.y((row.getCell(4).toString()))
						.inputArea(conditionA+" "+conditionB+" "+conditionC)
						.build();
			}
		}

		workbook.close();
//		fileInputStream.close();
		return null;
	}
}
