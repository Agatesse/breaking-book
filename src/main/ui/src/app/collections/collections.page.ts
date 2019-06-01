import {Component, OnInit} from '@angular/core';
import {Collection} from './collection.model';
import {Book} from '../library/book.model';
import {CollectionsService} from './services/collections.service';
import {BooksService} from '../library/services/books.service';
import {Router} from '@angular/router';
import {TokenStorageService} from '../authentication/services/token-storage.service';
import {ChooseBookModalComponent} from '../shared/modals/choose-book-modal/choose-book-modal.component';
import {ModalController} from '@ionic/angular';

@Component({
  selector: 'app-collections',
  templateUrl: './collections.page.html',
  styleUrls: ['./collections.page.scss'],
})
export class CollectionsPage implements OnInit {
  userInput: string = '';
  private _collections: Collection[] = [];
  private _booksCollectionsMap: Map<number, Book[]> = new Map<number, Book[]>();
  private _defaultCover = '../../../assets/default_cover.png';

  constructor(private collectionsService: CollectionsService,
              private booksService: BooksService,
              private router: Router,
              private tokenStorage: TokenStorageService,
              private modalCtrl: ModalController) {
  }


  get collections(): Collection[] {
    return this._collections;
  }

  get booksCollectionsMap(): Map<number, Book[]> {
    return this._booksCollectionsMap;
  }

  get defaultCover(): string {
    return this._defaultCover;
  }

  ngOnInit() {
    this.retrieveCollections();
  }

  retrieveCollections() {
    this.collectionsService.getCollections().subscribe(
      data => {
        this._collections = data;
        this.booksService.getBooks().subscribe(
          data => {
            this.initMap(data);

          }
        )
      },
      error => console.log(error)
    );
  }

  initMap(books: Book[]) {
    this.collections.forEach(collection => {
      if (collection.booksIds != null) {
        let bookList: Book[] = [];
        books.forEach(book => {
          if (collection.booksIds.includes(book.id)) {
            bookList.push(book);
          }
        });
        this._booksCollectionsMap.set(collection.id, bookList);
      }
    });
  }

  goToBookDetails(bookId: number) {
    this.router.navigate(['library', 'show', bookId])
  }

  openBookToCollectionModal(collection: Collection) {
    this.modalCtrl
      .create({
        component: ChooseBookModalComponent,
        componentProps: {modalMode: 'collection', collection: collection}
      })
      .then(modal => {
          modal.present();
          return modal.onDidDismiss();
        }
      )
      .then(modal => {
        if (modal.role == 'book') {
          this.addBookToCollection(modal.data, collection.id);
        }
      })
  }

  addBookToCollection(bookId: number, collectionId: number) {
    this.collectionsService.addBookToCollection(collectionId, bookId).subscribe(
      () => this.retrieveCollections(),
      error => console.log(error)
    )

  }

  createNewCollection() {
    if (this.userInput == '') {
      return;
    }
    let collection = new Collection(this.userInput, +this.tokenStorage.getUserId());
    this.collectionsService.saveCollection(collection).subscribe(
      () => this.retrieveCollections(),
      error => console.log(error)
    );
  }

  deleteBookFromLibrary(bookId: number) {
    console.log(bookId)
  }

  deleteCollection(collectionId: number) {
    this.collectionsService.deleteCollection(collectionId).subscribe(
      () => this.retrieveCollections(),
      error => console.log(error)
    );
  }
}
