package Parser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;


public class CvAnalyzer {

	private double score;
	private ArrayList<String> language = new ArrayList<String>();
	private ArrayList<String> qualification = new ArrayList<String>();
	private ArrayList<String> experience = new ArrayList<String>();
	private ArrayList<String> nationality = new ArrayList<String>();
	private ArrayList<String> CV = new ArrayList<String>();
	
	private ArrayList<String> qualificationsFulfilled = new ArrayList<String>();
	private ArrayList<String> experienceFulfilled = new ArrayList<String>();
	private ArrayList<String> languageFulfilled = new ArrayList<String>();
	private ArrayList<String> particularsFulfilled = new ArrayList<String>();
	
	
	// for headers
	private ArrayList<String> qualificationHeaders = new ArrayList<String>();
	private ArrayList<String> experienceHeaders = new ArrayList<String>();
	private ArrayList<String> languageHeaders = new ArrayList<String>();
	private ArrayList<String> particularsHeaders = new ArrayList<String>();
	
	public enum HeaderTypes {QUALIFICATION, EXPERIENCE, LANGUAGE, PARTICULARS, INVALID};
	
	private double computeTotalAttribute(){
		return (language.size() + qualification.size() + experience.size() + nationality.size());
	}
	
	private boolean checkAvail(){
		if (computeTotalAttribute() == 0)
			return false;
		else 
			return true;
	}
	
	public void inputCV(ArrayList<String> input){
		CV.clear();
		CV.addAll(input);
	}

	public double getScore(){
		double size = computeTotalAttribute();
		return (score/size)*100;	
	}
	
	public void clearLists(){
		language.clear();
		qualification.clear();
		experience.clear();
		nationality.clear();

		languageFulfilled.clear();
		qualificationsFulfilled.clear();
		experienceFulfilled.clear();
		particularsFulfilled.clear();
	}
	
	public void addLists( ArrayList<String> languageInput,ArrayList<String> qualificationInput,ArrayList<String> experienceInput,ArrayList<String> nationalityInput){
		language.addAll(languageInput);
		qualification.addAll(qualificationInput);
		experience.addAll(experienceInput);
		nationality.addAll(nationalityInput);	
	}

	public double execute(String path, ArrayList<String> languageInput,ArrayList<String> qualificationInput,ArrayList<String> experienceInput,ArrayList<String> nationalityInput) throws IOException
	{
		clearLists();
		loadAllHeaderTypes(path);
		addLists(languageInput,qualificationInput, experienceInput, nationalityInput);

		if (checkAvail() == true){
			score = 0;
			String paragraph = null;		
			// initialise header to be invalid
			HeaderTypes header = HeaderTypes.INVALID;
			for (int i = 0; i < CV.size(); i++){
				paragraph = CV.get(i);
				paragraph = paragraph.toLowerCase();
				header = checkForHeader(paragraph, header);
				if (header == HeaderTypes.QUALIFICATION)
				{
					matchQualificationDetails(paragraph);
					//matchRequirements(qualification, qualificationsFulfilled, paragraph);
				}
				else if (header == HeaderTypes.EXPERIENCE)
				{
					matchExperienceDetails(paragraph);
					//matchRequirements(experience, experienceFulfilled, paragraph);
				}
				else if (header == HeaderTypes.LANGUAGE)
				{
					//matchRequirements(language, languageFulfilled, paragraph);
					matchLanguageDetails(paragraph);
				}
				else if (header == HeaderTypes.PARTICULARS)
				{
					//matchRequirements(nationality, particularsFulfilled, paragraph);
					matchParticularDetails(paragraph);
				}
			}
			double size = computeTotalAttribute();
			return (score/size)*100;	
		}
		else
			return 0.0;
	}
	
	
	
	public void matchRequirements(ArrayList<String> requirements, ArrayList<String> requirementsFulfilled, String line)
	{
		for (int i = requirements.size()-1; i >= 0; i--)
		{
			String attribute = requirements.get(i);
			attribute = (attribute.toLowerCase()).trim();
			
			// short attribute
			// would be safer to get an exact match
			if (attribute.length() < 4)
			{
				ArrayList<String> words = new ArrayList<String>(Arrays.asList(line.split("\\p{Punct}| ")));
				for (String word : words)
				{		
					if (attribute.equals(word.trim()))
					{
						boolean isFulfilled = false;
						for (String requirement : requirementsFulfilled)
						{
							if (requirement.equals(attribute))
								isFulfilled = true;
						}
						if (isFulfilled == false)
							score++;
					}
				}
			}
			else if (line.contains(attribute) && !requirementsFulfilled.contains(attribute))
			{
				requirementsFulfilled.add(attribute);
				score++;
			}
		}
	}
	public void matchQualificationDetails(String line)
	{
		for (int i = qualification.size()-1; i >= 0; i--)
		{
			String attribute = qualification.get(i);
			attribute = (attribute.toLowerCase()).trim();
			if (line.contains(attribute) && !qualificationsFulfilled.contains(attribute))
			{
				qualificationsFulfilled.add(attribute);
				score++;
			}
		}
	}
	
	public void matchExperienceDetails(String line)
	{
		for (int i = experience.size()-1; i >= 0; i--)
		{
			String attribute = experience.get(i);
			attribute = (attribute.toLowerCase()).trim();
			if (line.contains(attribute) && !experienceFulfilled.contains(attribute))
			{
				experienceFulfilled.add(attribute);
				score++;
			}
		}
	}
	public void matchLanguageDetails(String line)
	{
		for (int i = language.size()-1; i >= 0; i--)
		{
			String attribute = language.get(i);
			attribute = (attribute.toLowerCase()).trim();
			if (line.contains(attribute) && !languageFulfilled.contains(attribute))
			{
				languageFulfilled.add(attribute);
				score++;
			}
		}
		
	}
	public void matchParticularDetails(String line)
	{
		for (int i = nationality.size()-1; i >= 0; i--)
		{
			String attribute = nationality.get(i);
			attribute = (attribute.toLowerCase()).trim();
			if (line.contains(attribute) && !particularsFulfilled.contains(attribute))
			{
				particularsFulfilled.add(attribute);
				score++;
			}
		}
		
	}
	
	// load all types predefined headers
	public void loadAllHeaderTypes(String path) throws FileNotFoundException, IOException
	{	
		String educationPath = path + "qualificationHeaders.txt";
		String experiencePath = path + "experienceHeaders.txt";
		String languagePath = path + "languageHeaders.txt";
		String particularsPath = path + "particularsHeaders.txt";
		
		loadHeaders(educationPath, qualificationHeaders);
		loadHeaders(experiencePath, experienceHeaders);
		loadHeaders(languagePath, languageHeaders);
		loadHeaders(particularsPath, particularsHeaders);
		
	}
	
	// load headers of a particular type
	public void loadHeaders(String headerFilePath, ArrayList<String> headerType) throws FileNotFoundException, IOException
	{
		FileReader fileReader = new FileReader(headerFilePath);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = "";
		// repeat until all headers are read
		while ((line = bufferedReader.readLine())!= null)
		{
			headerType.add(line);
		}
	
		bufferedReader.close();
	}
	
	
	public HeaderTypes checkForHeader(String line, HeaderTypes header)
	{
		// first and second check
		if (checkWordLimit(line) && checkForSymbols(line))
		{
			// passed first and second check
			if(checkForDefinedHeaders(line,qualificationHeaders)== true)
			{
				System.out.println("qualification header is " + line);
				return HeaderTypes.QUALIFICATION;
			}
			
			else if (checkForDefinedHeaders(line,experienceHeaders)== true)
			{
				System.out.println("experience header is " + line);
				return HeaderTypes.EXPERIENCE;
			}
			else if (checkForDefinedHeaders(line,languageHeaders)== true)
			{
				System.out.println("language header is " + line);
				return HeaderTypes.LANGUAGE;
			}
			
			else if (checkForDefinedHeaders(line,particularsHeaders)== true)
			{
				System.out.println("particulars header is " +line);
				return HeaderTypes.PARTICULARS;
			}
			else 
				return header;				
		}
	return header;
	}
	
	// header check 1
	// check if the sentence has less than 4 words
	public boolean checkWordLimit(String line)
	{
		
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(line.split("\\p{Punct}| ")));
		if (words.size() > 4)
			return false;
		else
		{
			return true;
		}
	}
	
	// header check 2
	// check if punctuation can be found in line
	// headers should not contain commas and full stops
	public boolean checkForSymbols(String line)
	{
		if (line.contains(",|."))
			return false;
		else
			return true;
	}
	
	// header check 3
	// check if sentence contains defined headers
	public boolean checkForDefinedHeaders(String line, ArrayList<String> headerType)
	{
		String checkHeader = line.toLowerCase().trim();

		for (String header : headerType)
		{
			if (checkHeader.contains(header.trim().toLowerCase()))
			{
				//System.out.println("test 2: sentence contains defined headers");
				return true;
			}
		}
		return false;
	}
	
}
