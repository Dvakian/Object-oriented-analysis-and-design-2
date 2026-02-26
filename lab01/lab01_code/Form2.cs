using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using static System.Windows.Forms.VisualStyles.VisualStyleElement;

namespace lab01_code
{
    public partial class Form2 : Form
    {
        private BindingList<CarPartsRow> partsList = new BindingList<CarPartsRow>();
        public class CarPartsRow
        {
            public string EngineInfo { get; set; }
            public string WheelInfo { get; set; }
        }
        class Engine
        {
            public string Name { get; set;  }
            public string Specs { get; set; }
        }
        class Wheel
        {
            public string Name { get; set; }
            public string Specs { get; set; }
        }
        public Form2()
        {
            InitializeComponent();

            button1.Text = "Перейти";
            button2.Text = "Закрыть";


            dataGridView1.AutoGenerateColumns = false;
            dataGridView1.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
            dataGridView1.Columns.Clear();

            dataGridView1.Columns.Add("Engine", "Двигатель");
            dataGridView1.Columns["Engine"].DataPropertyName = "EngineInfo";

            dataGridView1.Columns.Add("Wheel", "Калёса");
            dataGridView1.Columns["Wheel"].DataPropertyName = "WheelInfo";

            dataGridView1.DataSource = partsList;

            comboBox1.Items.Add("Таёта");
            comboBox1.Items.Add("Honda");
            comboBox1.TabIndex = 0;
        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            partsList.Clear();

            string brand = comboBox1.SelectedItem.ToString();

            Engine engine;
            Wheel wheel;

            if (brand == "Таёта")
            {
                engine = new Engine { Name = "AbobaAT", Specs = "BbobaAT"};
                wheel = new Wheel { Name = "AbobaBT", Specs = "BbobaBT" };
            }
            else
            {
                engine = new Engine { Name = "AbobaAH", Specs = "BbobaAH" };
                wheel = new Wheel { Name = "AbobaBH", Specs = "BbobaBH" };
            }

            partsList.Add(new CarPartsRow
            {
                EngineInfo = engine.Name,
                WheelInfo = wheel.Name
            });

            partsList.Add(new CarPartsRow
            {
                EngineInfo = engine.Specs,
                WheelInfo = wheel.Specs
            });
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Form1 form1 = new Form1();
            form1.Show();
            this.Hide();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void button3_Click(object sender, EventArgs e)
        {
            MessageBox.Show("Кнопка для заполнения пустоты в окне \nДо свидания");
        }

        private void Form2_closing(object sender, FormClosingEventArgs e)
        {
            Application.Exit();
        }
    }
}
