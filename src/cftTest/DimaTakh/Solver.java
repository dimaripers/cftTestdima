package cftTest.DimaTakh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Solver {

	private String sortDirection = "";
	private String dataType = "";
	private String OutputFileName = "";
	private ArrayList<String> InputFileNames;
	private String[] InputFileLine;
	private BufferedReader[] reader;
	private RandomAccessFile[] endReader;
	private long[] filePointer;
	private BufferedWriter writer;
	private int ArgumentCount = 0;


	public Solver(String[] args) {
		try {
			if (args.length < 3) throw new ArgumentException("less than 3 arguments");
			
			//направление сортировки
			if (args[0].equals("-a")) {
				this.sortDirection = "a";
				this.ArgumentCount++;
			} else if (args[0].equals("-d")) {
				this.sortDirection = "d";
				this.ArgumentCount++;
			} else {
				this.sortDirection = "a";
			}
			
			//тип данных
			if (args[this.ArgumentCount].equals("-i")) {
				this.dataType = "i";
				this.ArgumentCount++;
			} else if (args[ArgumentCount].equals("-s")) {
				this.dataType = "s";
				this.ArgumentCount++;
			} else {
				throw new ArgumentException("unknown data type");
			}
			
			//выходной файл
			this.OutputFileName = args[this.ArgumentCount];
			this.ArgumentCount++;
			
			//входные файлы
			this.InputFileNames = new ArrayList<>();
			for (int i = this.ArgumentCount; i < args.length; i++) {
				this.InputFileNames.add(args[this.ArgumentCount]);
				this.ArgumentCount++;	
			}
		} catch (ArgumentException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}	
	}
	
	public void start() {
		if (this.dataType.equals("i")) this.startI();
		else if (this.dataType.equals("s")) this.startS();
	}
	public void initPointer(int FileNumber) {
		for (int i = 0; i < FileNumber; i++) {
			long fileLength = (new File(this.InputFileNames.get(i)).length()) - 1;
			this.filePointer[i] = fileLength;
		}
		
	}
	//открываем файлы
	public void openFiles(int NumberOfFiles) {
		try {
			for (int i = 0; i < NumberOfFiles; i++) {
				BufferedReader br = new BufferedReader(new FileReader(this.InputFileNames.get(i)));
				this.reader[i] = br;
			}
			try {
				this.writer = new BufferedWriter(new FileWriter(this.OutputFileName));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	public void openFilesForReverse(int NumberOfFiles) {
		try {
			for (int i = 0; i < NumberOfFiles; i++) {
				RandomAccessFile raf = new RandomAccessFile(new File(this.InputFileNames.get(i)), "r");
				this.endReader[i] = raf;
			}
			try {
				this.writer = new BufferedWriter(new FileWriter(this.OutputFileName));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	//----------------------------ДЛЯ ЧИСЕЛ-----------------------
	public void startI() {
		//инициализируем переменные
		int NumberOfFiles = this.InputFileNames.size();
		this.InputFileLine = new String[NumberOfFiles];
		//открываем ридеры в зависимости от направления сортировки
		if (sortDirection == "a") {
			this.reader = new BufferedReader[NumberOfFiles];
			this.openFiles(NumberOfFiles);
			//читаем первые строки в файлах	
			for (int i = 0; i < NumberOfFiles; i++) {
				this.readLineI(i);
				if (this.reader.length < NumberOfFiles) {
					NumberOfFiles = this.reader.length;
					i--;
				}	
			}
			//если массив файлов нулевой, то фалы пустые
			try {
				if (this.reader.length == 0) throw new ArgumentException("empty input files");
			} catch (ArgumentException ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
				try {
					this.writer.flush();
					this.writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(1);
			}
			//начинаем слияние отсортированных файлов
			this.HalfMergeSortI();
		} else if (sortDirection == "d") {
			this.endReader = new RandomAccessFile[NumberOfFiles];
			this.filePointer = new long[NumberOfFiles];
			
			this.initPointer(NumberOfFiles);
			this.openFilesForReverse(NumberOfFiles);
			//читаем первые строки в файлах	
			for (int i = 0; i < NumberOfFiles; i++) {
				this.readLineId(i);
				if (this.endReader.length < NumberOfFiles) {
					NumberOfFiles = this.endReader.length;
					i--;
				}	
			}
			//если массив файлов нулевой, то фалы пустые
			try {
				if (this.endReader.length == 0) throw new ArgumentException("empty input files");
			} catch (ArgumentException ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
				try {
					this.writer.flush();
					this.writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(1);
			}
			//начинаем слияние отсортированных файлов
			this.HalfMergeSortId();
		}		
	}
	public void HalfMergeSortI() {	
		while (this.InputFileLine.length != 0) {
			int[] lineI = new int[this.InputFileLine.length];
			int min = Integer.parseInt(this.InputFileLine[0]);
			int minIndex = 0;
			for (int i = 0; i < lineI.length; i++) {
				lineI[i] = Integer.parseInt(this.InputFileLine[i]);
				if (min > lineI[i]) {
					min = lineI[i];
					minIndex = i;
				}
			}
			try {
				this.writer.write(String.valueOf(min) + "\n");
				this.writer.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			readLineI(minIndex);		
		}
		try {
			this.writer.flush();
			this.writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}	
	}
	public void HalfMergeSortId() {	
		while (this.InputFileLine.length != 0) {
			int[] lineI = new int[this.InputFileLine.length];
			int max = Integer.parseInt(this.InputFileLine[0]);
			int maxIndex = 0;
			for (int i = 0; i < lineI.length; i++) {
				lineI[i] = Integer.parseInt(this.InputFileLine[i]);
				if (max < lineI[i]) {
					max = lineI[i];
					maxIndex = i;
				}
			}
			try {
				this.writer.write(String.valueOf(max) + "\n");
				this.writer.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			readLineId(maxIndex);		
		}
		try {
			this.writer.flush();
			this.writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}	
	}
	public void readLineI(int FileNumber) {
		String line = "";
		//читаем строку
		try {
			line = this.reader[FileNumber].readLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// если не null проверяем на пробел в строке
		if (line != null) {
			if (line.split(" ").length == 1) {
			try {
					//запись строки и проверка является ли строка числом
					int temp = Integer.parseInt(line);
					this.InputFileLine[FileNumber] = line;
				} catch (NumberFormatException ex) {
					System.out.println(ex.getMessage());
					ex.printStackTrace();
					this.readLineI(FileNumber);
				}
			//если в строке пробел, то читаем следующую
			} else {
				this.readLineI(FileNumber);
			}
		//если строка null, считаем что файл кончился
		//закрываем его и удаляем из массива читаемых файлов
		} else {
			try {
				this.reader[FileNumber].close();
				BufferedReader[] temp = new BufferedReader[this.reader.length - 1];				
				System.arraycopy(this.reader, 0, temp, 0, FileNumber);
				System.arraycopy(this.reader, FileNumber + 1, temp, FileNumber, temp.length - FileNumber);
				this.reader = temp;
				String[] tempLine = new String[this.InputFileLine.length - 1];
				System.arraycopy(this.InputFileLine, 0, tempLine, 0, FileNumber);
				System.arraycopy(this.InputFileLine, FileNumber + 1, tempLine, FileNumber, tempLine.length - FileNumber);
				this.InputFileLine = tempLine;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void readLineId(int FileNumber) {
		if (this.filePointer[FileNumber] < 0){
			try {
				this.endReader[FileNumber].close();
				RandomAccessFile[] temp = new RandomAccessFile[this.endReader.length - 1];				
				System.arraycopy(this.endReader, 0, temp, 0, FileNumber);
				System.arraycopy(this.endReader, FileNumber + 1, temp, FileNumber, temp.length - FileNumber);
				this.endReader = temp;
				String[] tempLine = new String[this.InputFileLine.length - 1];
				System.arraycopy(this.InputFileLine, 0, tempLine, 0, FileNumber);
				System.arraycopy(this.InputFileLine, FileNumber + 1, tempLine, FileNumber, tempLine.length - FileNumber);
				this.InputFileLine = tempLine;
				long[] tempPointer = new long[this.filePointer.length - 1];
				System.arraycopy(this.filePointer, 0, tempPointer, 0, FileNumber);
				System.arraycopy(this.filePointer, FileNumber + 1, tempPointer, FileNumber, tempPointer.length - FileNumber);
				this.filePointer = tempPointer;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		StringBuilder builder = new StringBuilder();
		String line = "";
		try {
			this.endReader[FileNumber].seek(this.filePointer[FileNumber]);
			for (long point = this.filePointer[FileNumber]; point >= 0; point--){
				this.endReader[FileNumber].seek(point);
		        char c;
		        c = (char)this.endReader[FileNumber].read(); 
		        if(c == '\n'){
		        	this.filePointer[FileNumber]--;
		        	break;
		        }
		        if(c == '\r'){
		        	this.filePointer[FileNumber]--;
		        	this.readLineId(FileNumber);
		        	return;
		        }
		        builder.append(c);
		        this.filePointer[FileNumber]--;
			}
			builder.reverse();
			line = builder.toString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
		
		// если не null проверяем на пробел в строке
		if (line != null) {
			if (line.split(" ").length == 1) {
			try {
					//запись строки и проверка является ли строка числом
					int temp = Integer.parseInt(line);
					this.InputFileLine[FileNumber] = line;
				} catch (NumberFormatException ex) {
					System.out.println(ex.getMessage());
					ex.printStackTrace();
					this.readLineId(FileNumber);
				}
			//если в строке пробел, то читаем следующую
			} else {
				this.readLineId(FileNumber);
			}
		//если строка null, считаем что файл кончился
		//закрываем его и удаляем из массива читаемых файлов
		} else if ((line == null) | (this.filePointer[FileNumber] == 0)){
			try {
				this.endReader[FileNumber].close();
				RandomAccessFile[] temp = new RandomAccessFile[this.endReader.length - 1];				
				System.arraycopy(this.endReader, 0, temp, 0, FileNumber);
				System.arraycopy(this.endReader, FileNumber + 1, temp, FileNumber, temp.length - FileNumber);
				this.endReader = temp;
				String[] tempLine = new String[this.InputFileLine.length - 1];
				System.arraycopy(this.InputFileLine, 0, tempLine, 0, FileNumber);
				System.arraycopy(this.InputFileLine, FileNumber + 1, tempLine, FileNumber, tempLine.length - FileNumber);
				this.InputFileLine = tempLine;
				long[] tempPointer = new long[this.filePointer.length - 1];
				System.arraycopy(this.filePointer, 0, tempPointer, 0, FileNumber);
				System.arraycopy(this.filePointer, FileNumber + 1, tempPointer, FileNumber, tempPointer.length - FileNumber);
				this.filePointer = tempPointer;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//------------------------------ДЛЯ СТРОК--------------------
	public void startS() {
		//инициализируем переменные
		int NumberOfFiles = this.InputFileNames.size();
		this.InputFileLine = new String[NumberOfFiles];
		//открываем ридеры в зависимости от направления сортировки
		if (sortDirection == "a") {
			this.reader = new BufferedReader[NumberOfFiles];
			this.openFiles(NumberOfFiles);
			//читаем первые строки в файлах	
			for (int i = 0; i < NumberOfFiles; i++) {
				this.readLineS(i);
					if (this.reader.length < NumberOfFiles) {
						NumberOfFiles = this.reader.length;
						i--;
					}	
				}
			//если массив файлов нулевой, то фалы пустые
			try {
				if (this.reader.length == 0) throw new ArgumentException("empty input files");
			} catch (ArgumentException ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
				try {
					this.writer.flush();
					this.writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(1);
			}
			//начинаем слияние отсортированных файлов
			HalfMergeSortS();
		} else if (sortDirection == "d") {
			this.endReader = new RandomAccessFile[NumberOfFiles];
			this.filePointer = new long[NumberOfFiles];
			this.initPointer(NumberOfFiles);
			this.openFilesForReverse(NumberOfFiles);
			//читаем первые строки в файлах	
			for (int i = 0; i < NumberOfFiles; i++) {
				this.readLineSd(i);
				if (this.endReader.length < NumberOfFiles) {
					NumberOfFiles = this.endReader.length;
					i--;
				}	
			}
			//если массив файлов нулевой, то фалы пустые
			try {
				if (this.endReader.length == 0) throw new ArgumentException("empty input files");
			} catch (ArgumentException ex) {
				System.out.println(ex.getMessage());
				ex.printStackTrace();
				try {
					this.writer.flush();
					this.writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(1);
			}
			//начинаем слияние отсортированных файлов
			this.HalfMergeSortSd();
		}			
	}
	public void readLineSd(int FileNumber) {
		if (this.filePointer[FileNumber] < 0){
			try {
				this.endReader[FileNumber].close();
				RandomAccessFile[] temp = new RandomAccessFile[this.endReader.length - 1];				
				System.arraycopy(this.endReader, 0, temp, 0, FileNumber);
				System.arraycopy(this.endReader, FileNumber + 1, temp, FileNumber, temp.length - FileNumber);
				this.endReader = temp;
				String[] tempLine = new String[this.InputFileLine.length - 1];
				System.arraycopy(this.InputFileLine, 0, tempLine, 0, FileNumber);
				System.arraycopy(this.InputFileLine, FileNumber + 1, tempLine, FileNumber, tempLine.length - FileNumber);
				this.InputFileLine = tempLine;
				long[] tempPointer = new long[this.filePointer.length - 1];
				System.arraycopy(this.filePointer, 0, tempPointer, 0, FileNumber);
				System.arraycopy(this.filePointer, FileNumber + 1, tempPointer, FileNumber, tempPointer.length - FileNumber);
				this.filePointer = tempPointer;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		StringBuilder builder = new StringBuilder();
		String line = "";
		try {
			this.endReader[FileNumber].seek(this.filePointer[FileNumber]);
			for (long point = this.filePointer[FileNumber]; point >= 0; point--){
				this.endReader[FileNumber].seek(point);
		        char c;
		        c = (char)this.endReader[FileNumber].read(); 
		        if(c == '\n'){
		        	this.filePointer[FileNumber]--;
		        	break;
		        }
		        if(c == '\r'){
		        	this.filePointer[FileNumber]--;
		        	this.readLineSd(FileNumber);
		        	return;
		        }
		        builder.append(c);
		        this.filePointer[FileNumber]--;
			}
			builder.reverse();
			line = builder.toString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
		
		// если не null проверяем на пробел в строке
		if (line != null) {
			if (line.split(" ").length == 1) {
				//запись строки и проверка является ли строка числом
				this.InputFileLine[FileNumber] = line;
			//если в строке пробел, то читаем следующую
			} else {
				this.readLineSd(FileNumber);
			}
		//если строка null, считаем что файл кончился
		//закрываем его и удаляем из массива читаемых файлов
		} else if ((line == null) | (this.filePointer[FileNumber] == 0)){
			try {
				this.endReader[FileNumber].close();
				RandomAccessFile[] temp = new RandomAccessFile[this.endReader.length - 1];				
				System.arraycopy(this.endReader, 0, temp, 0, FileNumber);
				System.arraycopy(this.endReader, FileNumber + 1, temp, FileNumber, temp.length - FileNumber);
				this.endReader = temp;
				String[] tempLine = new String[this.InputFileLine.length - 1];
				System.arraycopy(this.InputFileLine, 0, tempLine, 0, FileNumber);
				System.arraycopy(this.InputFileLine, FileNumber + 1, tempLine, FileNumber, tempLine.length - FileNumber);
				this.InputFileLine = tempLine;
				long[] tempPointer = new long[this.filePointer.length - 1];
				System.arraycopy(this.filePointer, 0, tempPointer, 0, FileNumber);
				System.arraycopy(this.filePointer, FileNumber + 1, tempPointer, FileNumber, tempPointer.length - FileNumber);
				this.filePointer = tempPointer;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void readLineS(int FileNumber) {
		String line = "";
		//читаем строку
		try {
			line = this.reader[FileNumber].readLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// если не null проверяем на пробел в строке
		if (line != null) {
			if (line.split(" ").length == 1) {
				//запись строки
				this.InputFileLine[FileNumber] = line;	
			//если в строке пробел, то читаем следующую
			} else {
				this.readLineS(FileNumber);
			}
		//если строка null, считаем что файл кончился
		//закрываем его и удаляем из массива читаемых файлов
		} else {
			try {
				this.reader[FileNumber].close();
				BufferedReader[] temp = new BufferedReader[this.reader.length - 1];				
				System.arraycopy(this.reader, 0, temp, 0, FileNumber);
				System.arraycopy(this.reader, FileNumber + 1, temp, FileNumber, temp.length - FileNumber);
				this.reader = temp;
				String[] tempLine = new String[this.InputFileLine.length - 1];
				System.arraycopy(this.InputFileLine, 0, tempLine, 0, FileNumber);
				System.arraycopy(this.InputFileLine, FileNumber + 1, tempLine, FileNumber, tempLine.length - FileNumber);
				this.InputFileLine = tempLine;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void HalfMergeSortSd() {	
		while (this.InputFileLine.length != 0) {
			String max = this.InputFileLine[0];
			int maxIndex = 0;
			for (int i = 0; i < this.InputFileLine.length; i++) {
				if (max.compareTo(this.InputFileLine[i]) < 0) {
					max = this.InputFileLine[i];
					maxIndex = i;
				}
			}
			try {
				this.writer.write(max + "\n");
				this.writer.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			readLineSd(maxIndex);		
		}
		try {
			this.writer.flush();
			this.writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}	
	}
	public void HalfMergeSortS() {	
		while (this.InputFileLine.length != 0) {
			String min = this.InputFileLine[0];
			int minIndex = 0;
			for (int i = 0; i < this.InputFileLine.length; i++) {
				if (min.compareTo(this.InputFileLine[i]) > 0) {
					min = this.InputFileLine[i];
					minIndex = i;
				}
			}
			try {
				this.writer.write(min + "\n");
				this.writer.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			readLineS(minIndex);		
		}
		try {
			this.writer.flush();
			this.writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}	
	}
	//-------------------ВЫВОД АРГУМЕНТОВ В КОНСОЛЬ--------------------
	public void showArguments() {
		System.out.println("SortDirection: " + this.sortDirection);
		System.out.println("DataType: " + this.dataType);
		System.out.println("OutFileName: " + this.OutputFileName);
		System.out.println("InFileNames: ");
		for (int i = 0; i < this.InputFileNames.size(); i++) {
			System.out.println(this.InputFileNames.get(i));
		}
		System.out.println("Args count: " + this.ArgumentCount);
	}
	


}
