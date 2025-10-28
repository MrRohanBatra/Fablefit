import { Container, Button } from "react-bootstrap";
import FeaturedCategories from "./FeaturedCategories"; 
import './Home.css'; 
import SplitText from "../../ReactBits/SplitText/SplitText";
import {useNavigate } from "react-router";

function Home() {
    const heroImage = "https://images.unsplash.com/photo-1551488831-00ddcb6c6bd3?q=80&w=2070";
    const navi = useNavigate();
    return (
        <>
            <div 
                className="hero-section" 
                style={{ backgroundImage: `url(${heroImage})` }}
            >
                <Container className="hero-content text-center text-white">
                    <h1 className="display-3 fw-bolder"><SplitText text={"Elevate Your Style"}></SplitText></h1>
                    <p className="lead mx-auto mb-4" style={{ maxWidth: '600px' }}>
                        Discover the latest trends and timeless classics in our curated collection.
                    </p>
                    <Button variant="primary" size="lg" className="rounded-pill px-4 py-2 fw-bold" onClick={() => {
                        navi("/", { replace: false });
                        setTimeout(() => {
                            const el = document.getElementById("featured");
                            if(el){el.scrollIntoView({ behavior: "smooth", block: "start" });}
                        },100)
                    }}>
                        Shop Now
                    </Button>
                </Container>
                
            </div>
            <FeaturedCategories />
        </>
    );
}

export default Home;