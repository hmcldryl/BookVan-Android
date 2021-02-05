using BookVan.ViewModels;
using System.ComponentModel;
using Xamarin.Forms;

namespace BookVan.Views
{
    public partial class ItemDetailPage : ContentPage
    {
        public ItemDetailPage()
        {
            InitializeComponent();
            BindingContext = new ItemDetailViewModel();
        }
    }
}