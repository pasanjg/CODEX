package com.sliit.spm.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.sliit.spm.exception.ResourceNotFoundException;
import com.sliit.spm.exception.UnprocessableEntityException;
import com.sliit.spm.repo.FileRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private ApplicationContext ctx;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public String add(@RequestParam("fileName") String fileName, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty())
            throw new UnprocessableEntityException();

        GridFsOperations gridOperations = (GridFsOperations) ctx.getBean("gridFsTemplate");
        DBObject metaData = new BasicDBObject();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date now = new Date();
        metaData.put("createdAt",dateFormat.format(now));

        InputStream inputStream = new BufferedInputStream(file.getInputStream());

        //get file extention
        String ext = "." + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

        return gridOperations.store(inputStream, fileName + ext, file.getContentType(), metaData).toString();
    }

    @GetMapping("/{id}")
    public void download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response){

        GridFsOperations gridOperations = (GridFsOperations) ctx.getBean("gridFsTemplate");

        GridFSFile file = gridOperations.findOne(new Query().addCriteria(Criteria.where("_id").is(id)));

        downloadFIleIntoServer(id);

        if (file == null)
            throw new ResourceNotFoundException();
        try {
            response.setContentType(gridOperations.getResource(file).getContentType());
            response.setContentLength((new Long(file.getLength()).intValue()));
            response.setHeader("content-Disposition", "attachment; filename=" + file.getFilename());// "attachment;filename=test.xls"
            // copy it to response's OutputStream
            IOUtils.copyLarge(gridOperations.getResource(file).getInputStream(), response.getOutputStream());
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
    public String downloadFIleIntoServer(String id){
        GridFsOperations gridOperations = (GridFsOperations) ctx.getBean("gridFsTemplate");

        GridFSFile file = gridOperations.findOne(new Query().addCriteria(Criteria.where("_id").is(id)));

        String name = file.getObjectId().toString()+file.getFilename();

        InputStream inputStream = null;
        OutputStream os = null;
        try {
            inputStream = gridOperations.getResource(file).getInputStream();
            File nFile = new File(System.getProperty("user.dir")+"/temp/"+name);
            boolean r =nFile.createNewFile();
            os = new FileOutputStream(nFile,false);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                inputStream.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return name;

    }
}
