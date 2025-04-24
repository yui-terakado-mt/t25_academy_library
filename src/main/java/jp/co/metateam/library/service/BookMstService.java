package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;
    
    @Autowired
    public BookMstService(BookMstRepository bookMstRepository){
        this.bookMstRepository = bookMstRepository;
    }
    
    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findLimitedBook();
        List<BookMstDto> bookMstDtoList = new ArrayList<BookMstDto>();

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {
            BookMst book = books.get(i);
            BookMstDto bookMstDto = new BookMstDto();
            bookMstDto.setId(book.getId());
            bookMstDto.setIsbn(book.getIsbn());
            bookMstDto.setTitle(book.getTitle());
            bookMstDtoList.add(bookMstDto);
        }

        return bookMstDtoList;
    }
    public void save(BookMstDto bookMstDto){
        try{
            //newはクラスを作る
            BookMst book = new BookMst();
           //タイトル取り出し
            book.setTitle(bookMstDto.getTitle());
            book.setIsbn(bookMstDto.getIsbn());

            //データベースへの保存
            this.bookMstRepository.save(book);
        }catch(Exception e){
            throw e;
        }
       
    }
    public boolean isValidTitle(String title, Model model){
        if (StringUtils.isEmpty(title)){
            model.addAttribute("errTitle", "書籍名を入力してください");
            return true;
        }

            ////タイトルが255文字を超えている場合
            if (title.length()>255) {
                model.addAttribute("errTitle","タイトルは255文字以内で入力してください");
                return true;
            }   
        return false;
        }
    
    public boolean isValidIsbn(String isbn, Model model){
        if (StringUtils.isEmpty(isbn)){
            model.addAttribute("errIsbn", "ISBNを入力してください");
            return true;      
        }

        /////文字数チェック
        if (isbn.length()>13) {
            model.addAttribute("errIsbn","ISBNは13文字以内で入力してください");
            return true;   
            
        }

        ////ISBNの文字数チェック半角数字のみ
        if (isbn.matches("[0-9]+")) {
            model.addAttribute("errIsbn","ISBNは半角数字で入力してください");
            return true;  
            
        } 

        return false;
    }



         ///重複チェック用のメゾット
         
         
         public boolean selectByIsbn(String isbn, Model model) {
         List<BookMst>book =this.bookMstRepository.selectByIsbn(isbn);

         if ((book.size()!=0)) {
         
         
            model.addAttribute("errIsbn", "このISBNはすでに登録されています");
            return true;
         }
         
        return false;
    }


}






