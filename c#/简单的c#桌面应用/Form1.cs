using System;
using System.Windows.Forms;

namespace HelloWorldApp
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            label1.Text = "Hello, World!";
        }
    }
}
