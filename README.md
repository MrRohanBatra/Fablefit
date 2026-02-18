# 👕 Fablefit - E-commerce Clothing Platform (Frontend)

A modern, responsive e-commerce web application for clothing built with React and Vite, offering a seamless shopping experience with fast performance and intuitive design.

## 🚀 Features

- **Modern UI/UX**: Clean and intuitive interface for browsing clothing items
- **Fast Performance**: Built with Vite for lightning-fast development and production builds
- **Responsive Design**: Fully optimized for desktop, tablet, and mobile devices
- **Product Catalog**: Browse through various clothing categories
- **Shopping Cart**: Add, remove, and manage items in your cart
- **Product Details**: Detailed view of each clothing item with images and descriptions
- **Search & Filter**: Easily find products with search and filtering options

## 🛠️ Tech Stack

- **React** - Frontend framework for building user interfaces
- **Vite** - Next-generation frontend build tool
- **ESLint** - Code linting and quality assurance
- **Fast Refresh/HMR** - Instant feedback during development

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- [Node.js](https://nodejs.org/) (v16.0 or higher)
- [npm](https://www.npmjs.com/) or [yarn](https://yarnpkg.com/)

## 🔧 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/MrRohanBatra/Fablefit.git
   cd Fablefit
   ```

2. **Install dependencies**
   ```bash
   npm install
   # or
   yarn install
   ```

3. **Start the development server**
   ```bash
   npm run dev
   # or
   yarn dev
   ```

4. **Open your browser**
   Navigate to `http://localhost:5173` (or the port shown in your terminal)

## 📦 Available Scripts

- `npm run dev` - Start development server with hot module replacement
- `npm run build` - Build the project for production
- `npm run preview` - Preview the production build locally
- `npm run lint` - Run ESLint to check code quality

## 🏗️ Project Structure

```
Fablefit/
├── public/           # Static assets
├── src/
│   ├── assets/       # Images, fonts, and other assets
│   ├── components/   # Reusable React components
│   ├── pages/        # Page components
│   ├── App.jsx       # Main App component
│   └── main.jsx      # Entry point
├── index.html        # HTML template
├── package.json      # Dependencies and scripts
├── vite.config.js    # Vite configuration
└── README.md         # Project documentation
```

## 🌐 Deployment

### Build for Production

```bash
npm run build
```

The optimized production build will be generated in the `dist` folder.

### Deploy to Vercel

```bash
npm install -g vercel
vercel
```

### Deploy to Netlify

```bash
npm run build
# Then drag and drop the 'dist' folder to Netlify
```

## 🔌 Vite Plugins

This project uses the following official Vite plugins:

- **[@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react)** - Uses Babel for Fast Refresh
- **[@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc)** - Uses SWC for Fast Refresh (alternative)

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 Code Quality

This project uses ESLint to maintain code quality. Make sure to run `npm run lint` before committing your changes.

## 🐛 Known Issues

- [ ] None at the moment

## 🚧 Roadmap

- [ ] User authentication and authorization
- [ ] Payment gateway integration
- [ ] Order tracking system
- [ ] User reviews and ratings
- [ ] Wishlist functionality
- [ ] Backend API integration

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Rohan Batra**
- GitHub: [@MrRohanBatra](https://github.com/MrRohanBatra)
  
**Ayush Sharma**
- GitHub: [@AyushSharma](https://github.com/AyushSharma67)

## 🙏 Acknowledgments

- React team for the amazing framework
- Vite team for the blazing-fast build tool
- All contributors who help improve this project

## 📞 Contact & Support

If you have any questions or need support, please:
- Open an issue on GitHub
- Reach out via GitHub profile

---

⭐ If you find this project useful, please consider giving it a star on GitHub!
