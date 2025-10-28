// import { Container, Row, Col } from 'react-bootstrap';
// import './FeaturedCategories.css'; // We will create this CSS file next
// import { Link, useNavigate } from 'react-router';

// // Data for our categories. You can later fetch this from an API.
// const categories = [
//   {
//     name: "Men's Collection",
//     image: "https://images.pexels.com/photos/2379004/pexels-photo-2379004.jpeg?cs=srgb&dl=pexels-italo-melo-881954-2379004.jpg&fm=jpg"
//   },
//   {
//     name: "Women's Collection",
//     image: "https://media.istockphoto.com/id/1326417862/photo/young-woman-laughing-while-relaxing-at-home.jpg?s=612x612&w=0&k=20&c=cd8e6RBGOe4b8a8vTcKW0Jo9JONv1bKSMTKcxaCra8c="
//   },
//   {
//     name: "Kid's Collection",
//     image: "https://images.unsplash.com/photo-1627639679638-8485316a4b21?fm=jpg&q=60&w=3000&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Y3V0ZSUyMGtpZHxlbnwwfHwwfHx8MA%3D%3D"
//   },
// ];
// const trending = [
//     {
//         name: "Men's Textured Formal Blazer",
//         image:"https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcRCsZPsrhcEjOYsN8xNusbZuaL322lUjiXG8zTeXOFC4FC-1Lq5qAfwct8U5X5gcVfq0CFb_3WzUMMJaYBXkJ9O8337BENZ14DBSvhIh4c"
        
//     },
//     {
//         name: "Elegant Evening Dress",
//         image:"https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcSVdiveeV4k4YxRGTU-Ce6VDD53U76ZzR1U3kU6WQNtZUgK8kRvr1R8z256K64xvgi8mcX_VA8M4l-tnhwGhZ0ZO3Mh9OPHjPcvJ67Wq466LU1gj9tQnZMl0A"
        
//     },
//     {
//         name: "Kid's Casual wear ",
//         image:"https://image.hm.com/assets/hm/d4/2c/d42c7fee05728ac1489fb004a9cdc204eb3c7af8.jpg?imwidth=1536"
        
//     }
// ]
// function FeaturedCategories() {
//   const navi = useNavigate();
//   const navigateToSearch = (name) => { navi(`/search?s=${name}`); }
//   return (
//       <>
//       <section className="py-5" id='featured'>
//       <Container className="text-center">
//         <h2 className="fw-bold mb-5">Featured Categories</h2>
//         <Row>
          
//           {categories.map((category, index) => (
//             <Col md={4} key={index} className="mb-4">
//               <div  onClick={navigateToSearch(category.name)} className="category-card text-decoration-none">
//                 <div className="card-img-wrapper">
//                   <div 
//                     className="card-img" 
//                     style={{ backgroundImage: `url(${category.image})` }}
//                   ></div>
//                 </div>
//                 <p className="mt-3 fs-5 fw-semibold">{category.name}</p>
//               </div>
//             </Col>
//           ))}
//         </Row>
//       </Container>
//       </section>
//     <section className="py-5" id='trending'>
//       <Container className="text-center">
//         <h2 className="fw-bold mb-5">Trending Now</h2>
//         <Row>
         
//           {trending.map((category, index) => (
//             <Col md={4} key={index} className="mb-4">
//               <a as={Link} to={`/search?s=${category.name}`} className="category-card text-decoration-none">
//                 <div className="card-img-wrapper">
//                   <div 
//                     className="card-img" 
//                     style={{ backgroundImage: `url(${category.image})` }}
//                   ></div>
//                 </div>
//                 <p className="mt-3 fs-5 fw-semibold">{category.name}</p>
//               </a>
//             </Col>
//           ))}
//         </Row>
//       </Container>
//     </section>
//       </>
    
//   );
// }

// export default FeaturedCategories;
import { Container, Row, Col } from 'react-bootstrap';
import './FeaturedCategories.css';
import { Link, useNavigate } from 'react-router-dom';

// Data for categories
const categories = [
  {
    name: "Men's Collection",
    image: "https://images.pexels.com/photos/2379004/pexels-photo-2379004.jpeg?cs=srgb&dl=pexels-italo-melo-881954-2379004.jpg&fm=jpg"
  },
  {
    name: "Women's Collection",
    image: "https://media.istockphoto.com/id/1326417862/photo/young-woman-laughing-while-relaxing-at-home.jpg?s=612x612&w=0&k=20&c=cd8e6RBGOe4b8a8vTcKW0Jo9JONv1bKSMTKcxaCra8c="
  },
  {
    name: "Kid's Collection",
    image: "https://images.unsplash.com/photo-1627639679638-8485316a4b21?fm=jpg&q=60&w=3000&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Y3V0ZSUyMGtpZHxlbnwwfHwwfHx8MA%3D%3D"
  },
];

const trending = [
  {
    name: "Men's Textured Formal Blazer",
    image: "https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcRCsZPsrhcEjOYsN8xNusbZuaL322lUjiXG8zTeXOFC4FC-1Lq5qAfwct8U5X5gcVfq0CFb_3WzUMMJaYBXkJ9O8337BENZ14DBSvhIh4c"
  },
  {
    name: "Elegant Evening Dress",
    image: "https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcSVdiveeV4k4YxRGTU-Ce6VDD53U76ZzR1U3kU6WQNtZUgK8kRvr1R8z256K64xvgi8mcX_VA8M4l-tnhwGhZ0ZO3Mh9OPHjPcvJ67Wq466LU1gj9tQnZMl0A"
  },
  {
    name: "Kid's Casual wear",
    image: "https://image.hm.com/assets/hm/d4/2c/d42c7fee05728ac1489fb004a9cdc204eb3c7af8.jpg?imwidth=1536"
  }
];

function FeaturedCategories() {
  const navi = useNavigate();

  const navigateToSearch = (name) => {
    navi(`/search?s=${encodeURIComponent(name)}`);
  };

  return (
    <>
      {/* Featured Section */}
      <section className="py-5" id="featured">
        <Container className="text-center">
          <h2 className="fw-bold mb-5">Featured Categories</h2>
          <Row>
            {categories.map((category, index) => (
              <Col md={4} key={index} className="mb-4">
                <div
                  onClick={() => navigateToSearch(category.name)}
                  className="category-card text-decoration-none cursor-pointer"
                  role="button"
                >
                  <div className="card-img-wrapper">
                    <div
                      className="card-img"
                      style={{ backgroundImage: `url(${category.image})` }}
                    ></div>
                  </div>
                  <p className="mt-3 fs-5 fw-semibold">{category.name}</p>
                </div>
              </Col>
            ))}
          </Row>
        </Container>
      </section>

      {/* Trending Section */}
      <section className="py-5" id="trending">
        <Container className="text-center">
          <h2 className="fw-bold mb-5">Trending Now</h2>
          <Row>
            {trending.map((item, index) => (
              <Col md={4} key={index} className="mb-4">
                <Link
                  to={`/search?s=${encodeURIComponent(item.name)}`}
                  className="category-card text-decoration-none"
                >
                  <div className="card-img-wrapper">
                    <div
                      className="card-img"
                      style={{ backgroundImage: `url(${item.image})` }}
                    ></div>
                  </div>
                  <p className="mt-3 fs-5 fw-semibold">{item.name}</p>
                </Link>
              </Col>
            ))}
          </Row>
        </Container>
      </section>
    </>
  );
}

export default FeaturedCategories;
