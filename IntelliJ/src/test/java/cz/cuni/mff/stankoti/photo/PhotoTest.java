package cz.cuni.mff.stankoti.photo;

import cz.cuni.mff.stankoti.photo.util.FileSystem;
import cz.cuni.mff.stankoti.photo.db.DB;
import cz.cuni.mff.stankoti.photo.db.DBFile;
import cz.cuni.mff.stankoti.photo.status.StatusCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PhotoTest {
    static private final String TEST_DB_FILENAME = "photo_db_test.pdb";
    static private final String YELLOW_DOG = "C:\\pictures\\dogs\\yellow.png";
    static private final String YELLOW_DOG_COPY = "C:\\pictures\\dogs\\yellow-copy.png";
    static private final String BLACK_DOG = "C:\\pictures\\dogs\\black.png";

    static private DB db;

    @BeforeAll
    static public void setUp() {
        // initialize internal database and read data from the database file
        db = new DB(TEST_DB_FILENAME);
        assertEquals(StatusCode.NO_ERROR, db.getStatusCode(), "Error reading Photo Organizer test database file: " + TEST_DB_FILENAME);
    }

    @Test
    public void ReadFileInfoFromFileSystem() {
        // tests for FileInfo.extractFilename()
        assertEquals("filename.ext", FileSystem.extractFilename("filename.ext")); // filename only
        assertEquals("filename.ext", FileSystem.extractFilename("/dir/filename.ext")); // Linux
        assertEquals("filename.ext", FileSystem.extractFilename("\\dir\\filename.ext")); // Windows
        assertEquals("filename.ext", FileSystem.extractFilename("/dir/subdir/filename.ext")); // folder + subfolder

        // test for non-existent file
        DBFile file = FileSystem.getFileInformation("not-existent-file");
        assertEquals(StatusCode.FILE_SYSTEM_NOT_FILE, FileSystem.getStatusCode(), "The file 'not-existent-file' should not exist.");

        // tests for existing file
        assertEquals('F', FileSystem.checkPath(YELLOW_DOG), YELLOW_DOG + " should be a file.");
        file = FileSystem.getFileInformation(YELLOW_DOG);
        assertEquals(StatusCode.NO_ERROR, FileSystem.getStatusCode(), "The file should exist: " + YELLOW_DOG);
        assertEquals("yellow", file.getFilename(), "Invalid file name found.");
        assertEquals("png", file.getExtension(), "Invalid file extension found.");

        // compare files
        assertTrue(FileSystem.compareFiles(YELLOW_DOG, YELLOW_DOG_COPY), YELLOW_DOG + " and " + YELLOW_DOG_COPY + " should be copies.");
        assertFalse(FileSystem.compareFiles(YELLOW_DOG, BLACK_DOG), YELLOW_DOG + " and " + BLACK_DOG + " should not be the same.");
    }

    @Test
    public void ReadFileInfoFromDB() {
        // test for non-existent image in the internal DB
        int fileID = db.getFileID("not-existent-image.jpg");
        assertEquals(0, fileID, "The image 'not-existent-image.jpg' should not exist in the database.");

        // test for existing image in the internal DB
        fileID = db.getFileID(YELLOW_DOG);
        assertNotEquals(0, fileID, "The image should exist in the database: " + YELLOW_DOG);

        // some tests for accessing file information from internal DB
        DBFile file = db.getFile(fileID);
        assertNotNull(file, "The image with ID '" + fileID + "' should exist in the database.");
        assertEquals("yellow", file.getFilename(), "Invalid image name found.");
        assertEquals("png", file.getExtension(), "Invalid image extension found.");
    }

    @Test
    public void AddRemoveFile() {
        // get 'the yellow dog' image db File object and its ID
        int yellowDogFileID = db.getFileID(YELLOW_DOG);
        assertNotEquals(0, yellowDogFileID, "The image should exist in the database: " + YELLOW_DOG);
        DBFile yellowDogFile = db.getFile(yellowDogFileID);
        assertNotNull(yellowDogFile, "The image with ID '" + yellowDogFileID + "' should exist in the database.");

        // add existing file
        int oldFileID = yellowDogFileID;
        int newFileID = db.addFile(yellowDogFile); // adding an existing file => update file info & file ID is unchanged
        assertEquals(newFileID, oldFileID, "The file information should be updated and the file ID unchanged.");

        // remove file
        db.removeFile(yellowDogFileID);
        assertEquals(0, db.getFileID(YELLOW_DOG), "The file information should be removed from the database.");

        // now, add file again (now, as a new file)
        int nextFileID = db.nextFileID() + 1;
        int previousFileID = db.addFile(yellowDogFile); // adding an non-existing file => inseret file info & new file ID is assigned
        assertEquals(0, previousFileID, "The previous file ID should be 0 (0 => new file).");
        newFileID = db.getFileID(YELLOW_DOG);
        assertNotEquals(oldFileID, newFileID, "The file should get new file ID.");
        assertEquals(nextFileID, newFileID, "The file ID should be: " + nextFileID);
    }

    @Test
    public void AddRemoveKeyword() {
        // get 'the yellow dog' image db File object and its ID
        int yellowDogFileID = db.getFileID(YELLOW_DOG);
        assertNotEquals(0, yellowDogFileID, "The image should exist in the database: " + YELLOW_DOG);
        DBFile yellowDogFile = db.getFile(yellowDogFileID);
        assertNotNull(yellowDogFile, "The image with ID '" + yellowDogFileID + "' should exist in the database.");

        // get 'the black dog' image ID
        int blackDogFileID = db.getFileID(BLACK_DOG);
        assertNotEquals(0, blackDogFileID, "The image should exist in the database: " + BLACK_DOG);

        // remove keyword "yellow" from 'the yellow dog' image
        db.removeKeyword("yellow", yellowDogFileID);
        // check that
        Set<String> keywords = yellowDogFile.getKeywords(); // get all yellow dog keywords
        assertFalse(keywords.contains("yellow".toUpperCase()), "The yellow dog image should not be connected with the keyword 'yellow'.");

        // adds the keyword 'dog' to both yellow and black dog
        String keyword = "dog".toUpperCase();
        db.addKeyword(keyword, yellowDogFileID);
        db.addKeyword(keyword, blackDogFileID);
        // now, that keyword should be connected with both image IDs
        Set<Integer> fileIDs = db.getFileIDs(keyword, 'K'); // get all image IDs connected with keyword
        assertTrue(fileIDs.contains(yellowDogFileID), "Keyword '" + keyword + "' should be connected with the yellow dog image ID");
        assertTrue(fileIDs.contains(blackDogFileID), "Keyword '" + keyword + "' should be connected with the black dog image ID");

        List<String> allKeywords = db.getKeywords(); // get all keywords in the database
        // check if the keyword is in the list
        assertTrue(allKeywords.contains(keyword), "Keyword '" + keyword + "' should be in the keywords list.");
        // now, remove all current connection of that keyword
        // after that, keyword should be also removed from the database
        for (Integer fileID : new ArrayList<>(fileIDs)) {
            db.removeKeyword(keyword, fileID);
        }
        allKeywords = db.getKeywords(); // get again all keywords in the database
        // check if the keyword is in the list
        assertFalse(allKeywords.contains(keyword), "Keyword '" + keyword + "' should not be in the keywords list.");
    }

    @Test
    public void Duplicates() {
        // get 'the black dog' image ID
        int fileID = db.getFileID(BLACK_DOG);
        assertNotEquals(0, fileID, "The image should exist in the database: " + BLACK_DOG);
        // get 'the black dog' image File object
        DBFile fileObj = db.getFile(fileID);
        assertNotNull(fileObj, "The image with ID '" + fileID + "' should exist in the database.");

        // first, remove all duplicate information for the 'Black dog' image
        db.removeFileDuplicateInformation(fileObj);
        assertTrue(fileObj.getDuplicates().isEmpty(), "After removeFileDuplicateInformation(), the 'Black dog' image set of duplicates should be empty.");
        assertTrue(fileObj.getPotentialDuplicates().isEmpty(), "After removeFileDuplicateInformation(), the 'Black dog' image set of potential duplicates should be empty.");

        // now, find all duplicates of 'Black dog' image
        Map<Integer, Integer> duplicates = db.processDuplicates(fileID);
        // Map 'duplicates' should contain all 'Black dog' duplicates file IDs (as keys) including 'black dog' fileID
        Set<Integer> duplicateKeys = duplicates.keySet(); // extract keys from the map
        duplicateKeys.remove(fileID); // remove 'Black dog' fileID, leaving only the fileIDs of its duplicates

        // now, compare that set of keys with the result of getDuplicates() (it should be the same)
        Integer[] keyArray1 = duplicateKeys.toArray(new Integer[0]);
        Integer[] keyArray2 = fileObj.getDuplicates().toArray(new Integer[0]);
        java.util.Arrays.sort(keyArray1);
        java.util.Arrays.sort(keyArray2);
        assertArrayEquals(keyArray1, keyArray2, "The key sets should be equal.");
    }
}
